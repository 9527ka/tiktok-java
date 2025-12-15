package project.mall.comment;

import kernel.web.Page;
import project.mall.goods.model.SystemComment;
import project.web.admin.model.SystemCommentModel;

import java.util.List;

public interface AdminSystemCommentService {
    Page pagedQuery(int pageNo, int pageSize, Integer status);

    Page listComment(int pageNo, int pageSize, Integer status,String systemGoodId);


    void save(SystemComment comment);

    void saveUpdate(SystemCommentModel  model);


    SystemComment findCommentById(String id);

    List<SystemComment> queryTop50Comments(String systemGoodId , String sellerGoodsId);

    void delete(String id);

    void  deleteAll(String ids);

    void updateStatus(String id, int parseInt);
}
