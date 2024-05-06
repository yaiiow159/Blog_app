package com.blog.po;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tag")
public class TagPo extends BasicPo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "name")
    @NotBlank(message = "標籤名稱不能為空")
    private String name;

    @Column(name = "description")
    private String description;

    @ManyToMany(mappedBy = "tags", cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private List<PostPo> posts;

    @ManyToOne(fetch = FetchType.EAGER,optional = false)
    @JoinColumn(name = "category_id",
            foreignKey = @ForeignKey(name = "fk_tag_category"),
            referencedColumnName = "id")
    private CategoryPo category;
}
