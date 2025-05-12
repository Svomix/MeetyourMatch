package com.javanostra.spring.core.services;

import com.javanostra.spring.core.dao.GroupChatDao;
import com.javanostra.spring.core.dao.GroupMessageDAO;
import com.javanostra.spring.core.dao.UserDAO;
import com.javanostra.spring.core.entities.GroupChat;
import com.javanostra.spring.core.entities.GroupMessage;
import com.javanostra.spring.core.entities.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupChatService {
    private final GroupChatDao groupChatDao;
    private final UserService userService;
    private final UserDAO userDAO;
    private final GroupMessageDAO groupMessageDAO;

    public Long createGroupChat(List<Long> memberIds, String name, String avatar) {
        List<User> members = new ArrayList<>();
        for (Long memberId : memberIds) {
            members.add(userService.findUserById(memberId));
        }
        GroupChat groupChat = GroupChat.builder().members(members).name(name).build();
        if (avatar != null && !avatar.isEmpty())
            groupChat.setAvatarPath(avatar);
        groupChatDao.save(groupChat);
        return groupChat.getId();
    }

    public List<GroupMessage> getGroupMessages(Long id) {
        return groupChatDao.findAllByChatId(id);
    }

    public List<User> findAllGroupMembers(Long chatId) {
        return groupChatDao.findMembersById(chatId);
    }

    public GroupMessage saveGroupMessage(Long groupChatId, Long userId, String content) {
        User user = userDAO.findUserById(userId);
        if (user == null) throw new EntityNotFoundException("User not found with id: " + userId);
        GroupChat chat = groupChatDao.findById(groupChatId).orElseThrow(() -> new EntityNotFoundException("GroupChat not found with id: " + groupChatId));
        if (!chat.getMembers().contains(user)) {
            throw new IllegalStateException("User is not a member of this group chat");
        }
        GroupMessage message = new GroupMessage();
        message.setChat(chat);
        message.setUser(user);
        message.setContent(content);
        message.setTimestamp(new Timestamp(System.currentTimeMillis()));
        return groupMessageDAO.save(message);
    }

    public int exitGroupChat(Long groupChatId, User user) {
        return groupChatDao.exitFromChat(groupChatId, user.getId());
    }
    public int addGroupChat(Long groupChatId, User user) {
        return groupChatDao.addUserToChat(groupChatId, user.getId());
    }
}