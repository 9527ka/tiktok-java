package project.mall.banner;

import kernel.util.PageInfo;
import project.mall.banner.model.MallBanner;

import java.util.List;

public interface MallBannerService {

    List<MallBanner> getBannerList(String type, String imgType, PageInfo pageInfo);
}
