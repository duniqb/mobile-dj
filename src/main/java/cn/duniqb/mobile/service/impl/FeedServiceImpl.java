package cn.duniqb.mobile.service.impl;

import cn.duniqb.mobile.domain.WxUser;
import cn.duniqb.mobile.dto.json.JSONResult;
import cn.duniqb.mobile.nosql.mongodb.document.feed.Comment;
import cn.duniqb.mobile.nosql.mongodb.document.feed.Like;
import cn.duniqb.mobile.nosql.mongodb.document.feed.Title;
import cn.duniqb.mobile.service.FeedService;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.awt.print.Book;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author duniqb
 * @date 2019/12/30 22:39
 */
@Service
public class FeedServiceImpl implements FeedService {
    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 保存文章
     *
     * @param title
     * @return
     */
    @Override
    public Title save(Title title) {
        return mongoTemplate.save(title);
    }

    /**
     * 删除文章
     *
     * @param id
     * @return
     */
    @Override
    public DeleteResult delete(String id) {
        Title title = new Title();
        title.set_id(id);
        return mongoTemplate.remove(title);
    }

    /**
     * 分页查询文章
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public List<Title> listDesc(int pageNum, int pageSize) {
        List<Title> titleList;
        Query query = new Query();

        // 通过日期倒序
        query.with(Sort.by(Sort.Direction.DESC, "date"));

        if (pageNum != 1) {
            // number 参数是为了查上一页的最后一条数据
            int number = (pageNum - 1) * pageSize;
            query.limit(number);

            List<Title> titles = mongoTemplate.find(query, Title.class);
            // 取出最后一条
            Title title = titles.get(titles.size() - 1);

            // 取到上一页的最后一条数据 id，当作条件查接下来的数据
            String id = title.get_id();

            // 从上一页最后一条开始查（大于不包括这一条）
            query.addCriteria(Criteria.where("_id").gt(id));
        }
        // 页大小重新赋值，覆盖 number 参数
        query.limit(pageSize);
        // 即可得到第n页数据
        titleList = mongoTemplate.find(query, Title.class);

        return titleList;
    }

    /**
     * 根据 id 查询文章
     *
     * @param id
     * @return
     */
    @Override
    public Title findById(String id) {
        return mongoTemplate.findById(id, Title.class);
    }

    /**
     * 对文章点赞
     *
     * @param id
     * @param openid
     * @return
     */
    @Override
    public UpdateResult likeTitle(String id, String openid) {
        Title title = mongoTemplate.findById(id, Title.class);
        if (title != null) {
            Like like = new Like();
            like.setOpenid(openid);
            like.setTime(new Date());
            List<Like> likeList = title.getLikeList();
            // 判断重复点赞
            for (Like like1 : likeList) {
                if (like1.getOpenid().equals(openid)) {
                    return null;
                }
            }
            likeList.add(like);
            Query query = new Query(Criteria.where("_id").is(title.get_id()));
            Update update = new Update().set("likeList", likeList);
            // updateFirst 更新查询返回结果集的第一条
            return mongoTemplate.updateFirst(query, update, Title.class);
        }
        return null;
    }

    /**
     * 对文章取消点赞
     *
     * @param id
     * @param openid
     * @return
     */
    @Override
    public UpdateResult unlikeTitle(String id, String openid) {
        Title title = mongoTemplate.findById(id, Title.class);
        if (title != null) {
            List<Like> likeList = title.getLikeList();
            for (Like like : likeList) {
                if (like.getOpenid().equals(openid)) {
                    likeList.remove(like);
                    Query query = new Query(Criteria.where("_id").is(title.get_id()));
                    Update update = new Update().set("likeList", likeList);
                    // updateFirst 更新查询返回结果集的第一条
                    return mongoTemplate.updateFirst(query, update, Title.class);
                }
            }
        }
        return null;
    }

    /**
     * 添加评论
     *
     * @param openid
     * @param titleId
     * @param cmt
     * @param wxUser
     * @return
     */
    @Override
    public UpdateResult addComment(String openid, String titleId, String cmt, WxUser wxUser) {
        // 找出 mongoDB 中的文章
        Title title = findById(titleId);
        if (title != null) {
            Comment comment = new Comment();
            comment.setId(String.valueOf(System.currentTimeMillis()));
            comment.setAvatar(wxUser.getAvatarUrl());
            comment.setContent(cmt);
            comment.setOpenid(openid);
            comment.setDate(new Date());
            comment.setGroup(openid);
            comment.setState(0);
            comment.setNickname(wxUser.getNickname());

            List<Comment> commentList = title.getCommentList();
            commentList.add(comment);

            Query query = new Query(Criteria.where("_id").is(title.get_id()));
            Update update = new Update().set("commentList", commentList);
            // updateFirst 更新查询返回结果集的第一条
            return mongoTemplate.updateFirst(query, update, Title.class);

        }
        return null;
    }

    /**
     * 删除评论
     *
     * @param openid
     * @param titleId
     * @param commentId
     * @return
     */
    @Override
    public UpdateResult delComment(String openid, String titleId, String commentId) {
        // 找出 mongoDB 中的文章
        Title title = findById(titleId);
        if (title != null) {
            List<Comment> commentList = title.getCommentList();
            commentList.removeIf(comment -> comment.getId().equals(commentId) && comment.getOpenid().equals(openid));
            Query query = new Query(Criteria.where("_id").is(title.get_id()));
            Update update = new Update().set("commentList", commentList);
            // updateFirst 更新查询返回结果集的第一条
            return mongoTemplate.updateFirst(query, update, Title.class);
        }
        return null;
    }

    /**
     * 对评论点赞
     *
     * @param openid
     * @param titleId
     * @param commentId
     * @return
     */
    @Override
    public UpdateResult likeComment(String openid, String titleId, String commentId) {
        // 找出 mongoDB 中的文章
        Title title = findById(titleId);
        if (title != null) {
            List<Comment> commentList = title.getCommentList();
            Like like = new Like();
            like.setOpenid(openid);
            like.setTime(new Date());
            List<Like> likeList;
            for (Comment comment : commentList) {
                if (comment.getId().equals(commentId)) {
                    likeList = comment.getLikeList();
                    // 判断重复点赞
                    if (likeList != null) {
                        for (Like like1 : likeList) {
                            if (like1.getOpenid().equals(openid)) {
                                return null;
                            }
                        }
                    } else {
                        likeList = new ArrayList<>();
                        likeList.add(like);
                    }
                    comment.setLikeList(likeList);
                    break;
                }
            }
            Query query = new Query(Criteria.where("_id").is(title.get_id()));
            Update update = new Update().set("commentList", commentList);
            // updateFirst 更新查询返回结果集的第一条
            return mongoTemplate.updateFirst(query, update, Title.class);
        }
        return null;
    }

    /**
     * 取消评论点赞
     *
     * @param openid
     * @param titleId
     * @param commentId
     * @return
     */
    @Override
    public UpdateResult unlikeComment(String openid, String titleId, String commentId) {
        Title title = mongoTemplate.findById(titleId, Title.class);
        if (title != null) {
            List<Comment> commentList = title.getCommentList();
            for (Comment comment : commentList) {
                if (comment.getId().equals(commentId)) {
                    List<Like> likeList = comment.getLikeList();
                    for (Like like : likeList) {
                        if (like.getOpenid().equals(openid)) {
                            likeList.remove(like);
                            comment.setLikeList(likeList);

                            Query query = new Query(Criteria.where("_id").is(title.get_id()));
                            Update update = new Update().set("commentList", commentList);
                            // updateFirst 更新查询返回结果集的第一条
                            return mongoTemplate.updateFirst(query, update, Title.class);
                        }
                    }
                }
            }
        }
        return null;
    }
}
