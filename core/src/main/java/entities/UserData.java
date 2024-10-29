package entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_data")

public class UserData
{
    @Id
    @Column(name = "user_id")
    private Integer id;
    @Column(name = "user_name")
    private String userName;
    private String email;
    @Column(name = "avatar_path")
    private String avatarPath;
}
