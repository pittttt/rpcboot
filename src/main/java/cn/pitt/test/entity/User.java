package cn.pitt.test.entity;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class User implements Serializable {

	private static final long serialVersionUID = 7008065908084481927L;

	/** id */
	private Long id;

	/** 姓名 */
	private String name;

	/** 性别 */
	private Integer sex;

	/** 创建时间 */
	private Date createTime;

}
