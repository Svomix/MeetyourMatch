package com.javanostra.spring.core.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javanostra.spring.core.dao.*;
import com.javanostra.spring.core.dto.ChatInfoDTO;
import com.javanostra.spring.core.dto.FullUserProfileDTO;
import com.javanostra.spring.core.dto.UserProfileDTO;
import com.javanostra.spring.core.entities.*;
import com.javanostra.spring.core.enums.Relation;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsManager {

    @NonNull
    private final UserDAO userDAO;
    @NonNull
    private final UsersEventDAO usersEventDAO;
    @NonNull
    private final EventDAO eventDAO;
    @NonNull
    private final UsersAttributeValueDAO usersAttributeValueDAO;
    @NonNull
    private final TagDAO tagDAO;
    @NonNull
    private final TokenDAO tokenDAO;
    @NonNull
    private final ChatRoomDAO chatRoomDAO;
    @NonNull
    private final GroupChatDao UserGroupChatDao;
    @NonNull
    private final UserRelationDAO userRelationDAO;
    private final UserGroupChatDAO userGroupChatDAO;
    private final ChatMessageService chatMessageService;
    private final GroupChatDao groupChatDao;

    AuthenticationManager authenticationManager;

    private final SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();

    ObjectMapper mapper = new ObjectMapper();

    @Transactional
    @Override
    public void createUser(UserDetails user) {
        userDAO.save((User) user);
    }

    @Transactional
    @Override
    public void updateUser(UserDetails user) {
        //User user_ent = userDAO.findByUsername(user.getUsername());
        //user_ent.setPassword(user.getPassword());
        //user_ent.setAuthorities( userGroupDAO.findByAuthorityIn(user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList()) );
        userDAO.save((User) user);
    }

    @Transactional
    @Override
    public void deleteUser(String username) {
        User user_ent = userDAO.findByUsername(username);
        userDAO.delete(user_ent);
    }

    @Transactional
    public void delete(User user) {
        usersEventDAO.deleteAllByUserId(user.getId());
        usersAttributeValueDAO.deleteAllByUserId(user.getId());
        tokenDAO.deleteAllByUserId(user.getId());
        userDAO.delete(user);
    }

    @Transactional
    @Override
    public void changePassword(String oldPassword, String newPassword) {
        throw new UnsupportedOperationException();
    }

    protected Authentication createNewAuthentication(Authentication currentAuth, String newPassword) {
        UserDetails user = this.loadUserByUsername(currentAuth.getName());
        UsernamePasswordAuthenticationToken newAuthentication = UsernamePasswordAuthenticationToken.authenticated(user, (Object) null, user.getAuthorities());
        newAuthentication.setDetails(currentAuth.getDetails());
        return newAuthentication;
    }

    @Override
    public boolean userExists(String username) {
        return userDAO.existsByUsernameIgnoreCase(username);
    }

    public boolean userExistsByEmail(String email) {
        return userDAO.existsByEmailIgnoreCase(email);
    }

    public boolean userExistsById(Long id) {
        return userDAO.existsById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user_ent = userDAO.findByUsername(username);
        if (Objects.nonNull(user_ent)) {
            return user_ent;
        } else {
            throw new UsernameNotFoundException("username not found");
        }
    }

    public Page<UserProfileDTO> findAllUsers(Pageable pageable, Specification<User> specification) {
        return userDAO.findAll(specification, pageable).map(u -> mapper.convertValue(u, UserProfileDTO.class));
    }

    public User findUserById(Long userId) {
        return userDAO.findUserById(userId);
    }

    public User findByEmail(String email) {
        return userDAO.findByEmail(email);
    }

    public User findUserByUsername(String username) {
        return userDAO.findByUsername(username);
    }

    @Transactional
    public void saveUserEvent(UserActions event) {
        usersEventDAO.save(event);
    }

    @Transactional
    public void updateUserEvent(UserActions event) {
        usersEventDAO.save(event);
    }

    @Transactional
    public void deleteUserById(Long userId) {
        userDAO.deleteUserById(userId);
    }

    @Transactional
    public void deleteUserEventById(Long eventId) {
        usersEventDAO.deleteUserEventByEventId(eventId);
    }

    @Transactional
    public List<GroupChat> getGroupChat(Long userId) {
        return userGroupChatDAO.findAllByUserId(userId);
    }

    @Transactional
    public void createUserAttributeValue(Long userId, String attribute, String value) {
        UserAttribute userAttributeValue = new UserAttribute();
        userAttributeValue.setUser(userDAO.findUserById(userId));
        userAttributeValue.setAttribute(attribute);
        userAttributeValue.setValue(value);
        usersAttributeValueDAO.save(userAttributeValue);
    }

//    @Deprecated
//    public List<UserAttribute> getAttributes(User user){
//        return usersAttributeValueDAO.findByUser(user);
//    }
//
//    public List<UserAttribute> getAttributes(User user, String attribute){
//        return usersAttributeValueDAO.findByUserAndAttribute(user, attribute);
//    }

    @Transactional
    public void AddAttribute(User user, String attribute, String value) {
        UserAttribute userAttribute = new UserAttribute();
        userAttribute.setUser(user);
        userAttribute.setValue(value);
        userAttribute.setAttribute(attribute);
        usersAttributeValueDAO.save(userAttribute);
    }

    @Transactional
    public Relation getRelation(Long currentUserId, Long userId) {
        User currentUser = userDAO.findUserById(currentUserId);
        User user = userDAO.findUserById(userId);
        UserRelation userRelation = userRelationDAO.findByUserIdAndFriendId(currentUserId, userId);
        if (userRelation != null && userRelation.getIsAccepted()) {
            return Relation.FRIEND;
        }

        if (currentUser.getBlocked().contains(user)) {
            return Relation.BLOCKED;
        }

        return Relation.NONE;
    }

    public boolean checkIfInFriends(Long currentUserId, Long userId) {
        UserRelation userRelation = userRelationDAO.findByUserIdAndFriendId(currentUserId, userId);
        if (userRelation != null && userRelation.getIsAccepted()) {
            return true;
        }

        userRelation = userRelationDAO.findByUserIdAndFriendId(userId, currentUserId);

        if (userRelation != null && userRelation.getIsAccepted()) {
            return true;
        }

        return false;
    }

    public boolean checkIfInRequest(Long senderId, Long receiverId) {
        UserRelation userRelation = userRelationDAO.findByUserIdAndFriendId(senderId, receiverId);
        if (userRelation != null && !userRelation.getIsAccepted()) {
            return true;
        }

        return false;
    }

    @Transactional
    public boolean checkIfInBlocked(Long currentUserId, Long userId) {
        User currentUser = userDAO.findUserById(currentUserId);
        User user = userDAO.findUserById(userId);
        return currentUser.getBlocked().contains(user);
    }

    public Page<UserProfileDTO> getFriends(User currentUser, Pageable pageable, String namePattern) {
        List<User> friends = userRelationDAO.findFriendsByUserId(currentUser.getId())
                .stream()
                .filter(u -> u.getUsername().contains(namePattern))
                .toList();
        return new PageImpl<>(
                friends,
                pageable,
                friends.size()
        ).map(a -> mapper.convertValue(a, UserProfileDTO.class));
    }

    public Page<UserProfileDTO> getOutgoingFriendRequests(Long userId, Pageable pageable, String namePattern) {
        List<User> senders = userRelationDAO.findAllSendersByFriendId(userId)
                .stream()
                .filter(u -> u.getUsername().contains(namePattern))
                .toList();
        return new PageImpl<>(
                senders,
                pageable,
                senders.size()
        ).map(a -> mapper.convertValue(a, UserProfileDTO.class));
    }

    public Page<UserProfileDTO> getIngoingFriendRequests(Long userId, Pageable pageable, String namePattern) {
        List<User> receivers = userRelationDAO.findAllReceiversByUserId(userId)
                .stream()
                .filter(u -> u.getUsername().contains(namePattern))
                .toList();
        return new PageImpl<>(
                receivers,
                pageable,
                receivers.size()
        ).map(a -> mapper.convertValue(a, UserProfileDTO.class));
    }

    @Transactional
    public void sendFriendRequest(Long currentUserId, Long userId) {
        UserRelation userRelation = new UserRelation();
        userRelation.setUserId(currentUserId);
        userRelation.setFriendId(userId);
        userRelation.setIsAccepted(false);
        userRelationDAO.save(userRelation);
    }

    @Transactional
    public void acceptFriendRequest(Long currentUserId, Long userId) {
        UserRelation userRelation = userRelationDAO.findByUserIdAndFriendId(userId, currentUserId);
        userRelation.setIsAccepted(true);
        userRelationDAO.save(userRelation);
    }

    @Transactional
    public void deleteFriendRequest(Long currentUserId, Long userId) {
        userRelationDAO.deleteByUserIdAndFriendId(userId, currentUserId);
    }

    @Transactional
    public void deleteFriend(User currentUser, User friend) {
        userRelationDAO.deleteByUserIdAndFriendId(currentUser.getId(), friend.getId());
        userRelationDAO.deleteByUserIdAndFriendId(friend.getId(), currentUser.getId());
    }

    @Transactional
    public Page<UserProfileDTO> getBlocked(User currentUser, Pageable pageable, String namePattern) {
        User user = userDAO.findUserById(currentUser.getId());
        List<User> blocked = user.getBlocked()
                .stream()
                .filter(u -> u.getUsername().contains(namePattern))
                .toList();
        return new PageImpl<>(
                blocked,
                pageable,
                blocked.size()
        ).map(a -> mapper.convertValue(a, UserProfileDTO.class));
    }

    @Transactional
    public List<ChatInfoDTO> getUserChats(String username) {
        List<ChatRoom> chatRooms = chatRoomDAO.findById(username);
        List<GroupChat> groupChats = userGroupChatDAO.findAllByUserId(userDAO.findByUsername(username).getId());
        List<ChatInfoDTO> chats = new ArrayList<>();
        for (ChatRoom chatRoom : chatRooms) {
            if (chatRoom.getRecipientId().equals(username)) {
                String otherUser = chatRoom.getSenderId();
                List<ChatMessage> messages = chatMessageService.findChatMessages(otherUser, username);
                ChatMessage lastMessage = messages.stream()
                        .max(Comparator.comparing(ChatMessage::getTimestamp))
                        .orElse(null);

                User otherUserClass = userDAO.findByUsername(chatRoom.getSenderId());
                ChatInfoDTO chat = ChatInfoDTO.builder()
                        .id(otherUserClass.getId())
                        .avatarPath(otherUserClass.getAvatarPath())
                        .isGroup(false)
                        .username(otherUser)
                        .lastMessage(lastMessage != null ? lastMessage.getContent() : null)
                        .lastMessageTime(lastMessage != null ? lastMessage.getTimestamp() : null)
                        .build();

                chats.add(chat);
            } else if (chatRoom.getSenderId().equals(username)) {
                String otherUser = chatRoom.getRecipientId();
                List<ChatMessage> messages = chatMessageService.findChatMessages(username, otherUser);
                ChatMessage lastMessage = messages.stream()
                        .max(Comparator.comparing(ChatMessage::getTimestamp))
                        .orElse(null);

                User otherUserClass = userDAO.findByUsername(chatRoom.getRecipientId());
                ChatInfoDTO chat = ChatInfoDTO.builder()
                        .id(otherUserClass.getId())
                        .avatarPath(otherUserClass.getAvatarPath())
                        .isGroup(false)
                        .username(otherUser)
                        .lastMessage(lastMessage != null ? lastMessage.getContent() : null)
                        .lastMessageTime(lastMessage != null ? lastMessage.getTimestamp() : null)
                        .build();

                chats.add(chat);
            }
        }
        for (GroupChat groupChat : groupChats) {
            List<GroupMessage> messages = groupChatDao.findAllByChatId(groupChat.getId());
            GroupMessage lastMessage = messages.stream()
                    .max(Comparator.comparing(GroupMessage::getTimestamp))
                    .orElse(null);
            ChatInfoDTO chat = ChatInfoDTO.builder().avatarPath(groupChat.getAvatarPath()).isGroup(true).id(groupChat.getId()).username(groupChat.getName()).lastMessage(lastMessage != null ? lastMessage.getContent() : null).lastMessageTime(lastMessage != null ? lastMessage.getTimestamp() : null).build();
            chats.add(chat);
        }
        return chats;
    }


    @Transactional
    public void addBlocked(User currentUser, User blocked) {
        User user = userDAO.findUserById(currentUser.getId());
        user.getBlocked().add(blocked);
        userRelationDAO.deleteByUserIdAndFriendId(currentUser.getId(), blocked.getId());
        userRelationDAO.deleteByUserIdAndFriendId(blocked.getId(), currentUser.getId());
        userDAO.save(user);
    }

    @Transactional
    public void deleteBlocked(User currentUser, User blocked) {
        User user = userDAO.findUserById(currentUser.getId());
        user.getBlocked().remove(blocked);
        userDAO.save(user);
    }

    public FullUserProfileDTO getFullUserInfo(User user) {
        return mapper.convertValue(user, FullUserProfileDTO.class);
    }

    public User getCurrentUser() {
        SecurityContext ctx = securityContextHolderStrategy.getContext();
        if (Objects.nonNull(ctx)) {
            Authentication authentication = ctx.getAuthentication();
            Object principal = authentication.getPrincipal();

            if (principal instanceof User user) {
                return user;
            }
        }
        return null;
    }

    @Transactional
    public ResponseEntity<String> setDescription(String description) {
        User currentUser = getCurrentUser();
        if (currentUser != null) {
            currentUser.setDescription(description);
            userDAO.save(currentUser);
            return ResponseEntity.ok(description);
        }
        return ResponseEntity.noContent().build();
    }

    @Transactional
    @Scheduled(cron = "0 0 2 * * ?")
    @Async
    public void deleteUnconfirmedAccounts() {
        LocalDateTime filterTime = LocalDateTime.now().minusHours(2);
        List<User> unconfirmedUsers = userDAO.findAllByIsEnabledFalseAndCreatedAtBefore(filterTime);
        for (User user : unconfirmedUsers) {
            delete(user);
        }
    }
}
