package cn.duniqb.mobile.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author duniqb
 * @email duniqb@qq.com
 * @date 2020-06-27 16:27:31
 */
@Data
@TableName("dj_admin")
public class AdminEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId
    private Integer id;
    /**
     * openid
     */
    private String openid;

}
