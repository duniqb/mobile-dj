package cn.duniqb.mobile.service.impl;

import cn.duniqb.mobile.nosql.mongodb.document.feed.Title;
import cn.duniqb.mobile.service.FeedService;
import com.mongodb.client.result.DeleteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author duniqb
 * @date 2019/12/30 22:39
 */
@Service
public class FeedServiceImpl implements FeedService {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Title save(Title title) {
        return mongoTemplate.save(title);
    }

    @Override
    public DeleteResult delete(String id) {
        Title title = new Title();
        title.set_id(id);
        return mongoTemplate.remove(title);
    }

    @Override
    public List<Title> listDesc(int pageNum, int pageSize) {
        List<Title> titleList;

        Query query = new Query();

        // 通过 _id 来排序
        query.with(Sort.by(Sort.Direction.ASC, "_id"));

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

    @Override
    public Title findById(String id) {
        return mongoTemplate.findById(id, Title.class);
    }
}
