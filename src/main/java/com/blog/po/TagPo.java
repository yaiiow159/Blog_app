package com.blog.po;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tag", schema = "blog_app", catalog = "blog_app")
public class TagPo extends BasicPo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @ManyToMany(mappedBy = "tags", cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private List<PostPo> posts;

    @ManyToOne(fetch = FetchType.EAGER,optional = false)
    @JoinColumn(name = "category_id",
            foreignKey = @ForeignKey(name = "fk_tag_category"),
            referencedColumnName = "id",nullable = true)
    private CategoryPo category;
}
