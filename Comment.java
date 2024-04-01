package com.projectgroup.project.Model;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "sequence-generator"
    )
    @SequenceGenerator(
            name = "sequence-generator",
            sequenceName = "idOfComment"
    )
    int idOfComment;
    int id;
    String username = "";
    String content = "";
    String time = "";
    int like_cnt;
//    String img = ""; // 这是头像, 删除，获取的时候直接获取用户头像
    int type;
}
