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
public class Post {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "sequence-generator"
    )
    @SequenceGenerator(
            name = "sequence-generator",
            sequenceName = "id"
    )
    int id;
    String title = "";
    String username = "";
    String content = "";
    String time = "";
    int comment_cnt;
    int like_cnt;
    int star_cnt;
    String pngs = "";
    String tags = "";
}
