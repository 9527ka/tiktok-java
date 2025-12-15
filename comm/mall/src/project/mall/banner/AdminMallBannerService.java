package project.mall.banner;


import kernel.web.Page;
import project.mall.banner.model.MallBanner;
import project.news.News;

public interface AdminMallBannerService {
    Page pagedQuery(int pageNo, int pageSize, String type, String endTime, String startTime);

    void save(MallBanner banner);

    MallBanner findById(String id);

    void update(MallBanner banner);

    void delete(MallBanner banner);
}
