package project.web.api.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import kernel.util.ImageDispatcher;
import kernel.util.JsonUtils;
import kernel.util.PropertiesLoaderUtils;
import kernel.web.BaseAction;
import kernel.web.ResultObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.providers.encoding.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import project.mall.goods.SellerGoodsService;
import project.mall.goods.dto.SellerTopNDto;
import project.mall.goods.model.SystemGoods;
import project.mall.seller.dto.MallLevelCondExpr;
import project.mall.utils.MallPageInfo;
import project.onlinechat.OnlineChatUserMessageService;
import project.wallet.WalletLogService;
import project.web.api.impl.AwsS3OSSFileService;
import security.SaltSigureUtils;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.S3Exception;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

/**
 * 不方便测试的服务可以在此处统一调试
 *
 */
@RestController
@CrossOrigin
public class DemoController extends BaseAction {
	
	private Logger logger = LoggerFactory.getLogger(DemoController.class);
	
	@Autowired
    protected OnlineChatUserMessageService onlineChatUserMessageService;

	@Autowired
    protected WalletLogService walletLogService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SellerGoodsService sellerGoodsService;

    @Autowired
    AwsS3OSSFileService awsS3OSSFileService;
    private static Properties properties = PropertiesLoaderUtils.loadProperties("config/system.properties");

    private Set<String> invalidImageGoodsIds = new HashSet<>();
    private int invalidImageGoodsLoopCount = 0;

    @Value("${oss.aws.s3.bucketName}")
    private String bucketName;

	private final String action = "api/demo!";

    public static void main(String[] args) {
        List<String> imageList = new ArrayList<>();
        imageList.add("https://argos-shop-online.s3.amazonaws.com/pachong/gaoqing/B00005UP2P/81oU%2BCBYDOL._AC_SL1500_.jpg");
        imageList.add("https://argos-shop-online.s3.amazonaws.com/test/2023-03-28/80f63033-0251-4fc0-bc24-8d15b09d36e0.jpg");
        imageList.add("https://argos-shop-online.s3.amazonaws.com/test/2023-03-28/246929e9-9468-41fe-9713-00d855f97c71.jpg");
        imageList.add("https://argos-shop-online.s3.amazonaws.com/test/2023-03-28/099d9048-7a70-4c82-bd64-3fdbae771084.jpg");
        imageList.add("https://argos-shop-online.s3.amazonaws.com/test/2023-03-28/565d8792-5f95-48d5-bad9-c72473f648f4.jpg");
        imageList.add("https://argos-shop-online.s3.amazonaws.com/test/2023-03-28/c344eafc-af13-4721-ae91-1b847b775a0f.jpg");
        imageList.add("https://argos-shop-online.s3.amazonaws.com/test/2023-03-28/d7ff7be8-d0f6-49f8-8508-7a41f0827ea5.jpg");
        imageList.add("https://argos-shop-online.s3.amazonaws.com/test/2023-03-28/9b1c2a0b-023a-4aa8-9a45-888cba61ec7c.jpg");

        DemoController test = new DemoController();
        for (String oneImage : imageList) {
            test.checkFileExist(oneImage);
        }

//        String json = "{\"params\":[{\"code\":\"rechargeAmount\",\"title\":\"运行资金\",\"value\":5000},{\"code\":\"popularizeUserCount\",\"title\":\"分店数\",\"value\":3}],\"expression\":\"popularizeUserCount >= 3 || rechargeAmount >= 5000\"}";
//        MallLevelCondExpr cndObj = JsonUtils.json2Object(json, MallLevelCondExpr.class);
//
//        System.out.println("======> cndObj.param1:" + JsonUtils.getJsonString(cndObj.getParams().get(0)));
//
//
//        ProfileCredentialsProvider credentialsProvider = ProfileCredentialsProvider.create();
//        Region region = Region.US_EAST_1;
//        S3Client s3Client = S3Client.builder()
//                .region(region)
//                .credentialsProvider(credentialsProvider)
//                .build();
//
//        // 有： https://argos-shop-online.s3.amazonaws.com/pc/gp/B09YHXXL13/81tADIHjrGL._AC_SL1500_.jpg
//        // 无： https://argos-shop-online.s3.amazonaws.com/pc/gp/B0B8MZVBG9/61ex5gz5LpL._AC_SL1500_.jpg
//        String imagePath = "https://argos-shop-online.s3.amazonaws.com/pc/gp/B09YHXXL13/81tADIHjrGL._AC_SL1500_.jpg";
//        //ImageDispatcher.findFile(imagePath);
//
//        try {
//            HeadObjectRequest objectRequest = HeadObjectRequest
//                    .builder()
//                    .key("/pc/gp/B09YHXXL13/81tADIHjrGL._AC_SL1500_.jpg")
//                    .bucket("argos-shop-online")
//                    .build();
//
//            s3Client.headObject(objectRequest);
//            System.out.println("---> 图片存在");
//        } catch (NoSuchKeyException e) {
//            e.printStackTrace();
//        } catch (S3Exception e) {
//            e.printStackTrace();
//        } finally {
//            if(s3Client != null){
//                s3Client.close();
//            }
//        }
    }

    @GetMapping(action + "double.show")
    public ResultObject testDouble(HttpServletRequest request) {
        String oriDoubleValue = request.getParameter("value");
        double v1 = Double.parseDouble(oriDoubleValue);
        BigDecimal v2 = BigDecimal.valueOf(v1).setScale(2, BigDecimal.ROUND_DOWN);

        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        Map<String, Object> retData = new HashMap<>();
        resultObject.setData(retData);

        retData.put("oriValue", oriDoubleValue);
        retData.put("doubleValue", v1);
        retData.put("bigDecimalleValue", v2);

        return resultObject;
    }

    @GetMapping(action + "demo1.show")
    public Object listNotificationTemplate(HttpServletRequest request) {
        String buyerId = request.getParameter("buyerId");
        String sellerId = request.getParameter("sellerId");
        String userId = request.getParameter("userId");
        String account = request.getParameter("account");
        String password = request.getParameter("password");

        String password_encoder1 = passwordEncoder.encodePassword(password, account);
        String password_encoder2 = passwordEncoder.encodePassword(password, SaltSigureUtils.saltfigure);

        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
//        OnlineChatUserMessage lastImInfo = onlineChatUserMessageService.lastImMessage(buyerId, sellerId, false, 0L);
//        logger.info("-----> lastImInfo:" + lastImInfo);

//        List<Map<String, Object>> data = this.walletLogService.pagedQueryRecharge(1, 10, userId, "1").getElements();
//        for (Map<String, Object> log : data) {
//            if (null == log.get("coin") || !StringUtils.isNotEmpty(log.get("coin").toString())) {
//                log.put("coin", Constants.WALLET);
//            } else {
//                log.put("coin", log.get("coin").toString().toUpperCase());
//            }
//            String state = log.get("state").toString();
//            Object reviewTime = log.get("reviewTime");
//            log.put("reviewTime", reviewTime);
//        }
//        resultObject.setData(data);

        Map<String, Object> mockData = new HashMap<>();
        resultObject.setData(mockData);

        mockData.put("safePass1", password_encoder1);
        mockData.put("safePass2", password_encoder2);

        return resultObject;
    }


    @GetMapping(action + "demo2.show")
    public Object demo2(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();

        String startTime = "2023-01-01 00:00:00";
        String endTime = "2023-08-01 00:00:00";

        List<SellerTopNDto> top10SellerList = sellerGoodsService.cacheTopNSellers(startTime, endTime, 10);

        return resultObject;
    }

    @GetMapping(action + "invalidImageGoods.list")
    public Object invalidImageGoods(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();

        int pageNum = 1;
        int pageSize = 20000;
        MallPageInfo mallPageInfo  = sellerGoodsService.tmpPageListAllSystemGoods(pageNum, pageSize, "0", "0");
        List<SystemGoods> pageList = mallPageInfo.getElements();
        invalidImageGoodsLoopCount = 0;

        while (CollectionUtil.isNotEmpty(pageList)) {
            try {
                for (SystemGoods oneSystemGoods : pageList) {
                    invalidImageGoodsLoopCount++;
                    if (StrUtil.isNotBlank(oneSystemGoods.getImgUrl1())) {
                        boolean exist = checkFileExist(oneSystemGoods.getImgUrl1().trim());
                        if (!exist) {
                            invalidImageGoodsIds.add(oneSystemGoods.getId().toString());
                        }
                    }
                    if (StrUtil.isNotBlank(oneSystemGoods.getImgUrl2())) {
                        boolean exist = checkFileExist(oneSystemGoods.getImgUrl2().trim());
                        if (!exist) {
                            invalidImageGoodsIds.add(oneSystemGoods.getId());
                        }
                    }
                    if (StrUtil.isNotBlank(oneSystemGoods.getImgUrl3())) {
                        boolean exist = checkFileExist(oneSystemGoods.getImgUrl3().trim());
                        if (!exist) {
                            invalidImageGoodsIds.add(oneSystemGoods.getId().toString());
                        }
                    }
                    if (StrUtil.isNotBlank(oneSystemGoods.getImgUrl4())) {
                        boolean exist = checkFileExist(oneSystemGoods.getImgUrl4().trim());
                        if (!exist) {
                            invalidImageGoodsIds.add(oneSystemGoods.getId().toString());
                        }
                    }
                    if (StrUtil.isNotBlank(oneSystemGoods.getImgUrl5())) {
                        boolean exist = checkFileExist(oneSystemGoods.getImgUrl5().trim());
                        if (!exist) {
                            invalidImageGoodsIds.add(oneSystemGoods.getId().toString());
                        }
                    }
                    if (StrUtil.isNotBlank(oneSystemGoods.getImgUrl6())) {
                        boolean exist = checkFileExist(oneSystemGoods.getImgUrl6().trim());
                        if (!exist) {
                            invalidImageGoodsIds.add(oneSystemGoods.getId().toString());
                        }
                    }
                    if (StrUtil.isNotBlank(oneSystemGoods.getImgUrl7())) {
                        boolean exist = checkFileExist(oneSystemGoods.getImgUrl7().trim());
                        if (!exist) {
                            invalidImageGoodsIds.add(oneSystemGoods.getId().toString());
                        }
                    }
                    if (StrUtil.isNotBlank(oneSystemGoods.getImgUrl8())) {
                        boolean exist = checkFileExist(oneSystemGoods.getImgUrl8().trim());
                        if (!exist) {
                            invalidImageGoodsIds.add(oneSystemGoods.getId().toString());
                        }
                    }
                    if (StrUtil.isNotBlank(oneSystemGoods.getImgUrl9())) {
                        boolean exist = checkFileExist(oneSystemGoods.getImgUrl9().trim());
                        if (!exist) {
                            invalidImageGoodsIds.add(oneSystemGoods.getId().toString());
                        }
                    }
                    if (StrUtil.isNotBlank(oneSystemGoods.getImgUrl10())) {
                        boolean exist = checkFileExist(oneSystemGoods.getImgUrl10().trim());
                        if (!exist) {
                            invalidImageGoodsIds.add(oneSystemGoods.getId().toString());
                        }
                    }

                    logger.info("---> 验证了系统商品:{} 的图片问题，当前商品图片有效性状态:{}, 已遍历商品数量:{}", oneSystemGoods.getId(), invalidImageGoodsIds.contains(oneSystemGoods.getId().toString()) ? "存在无效图片" : "均有效", invalidImageGoodsLoopCount);
                }

                pageNum++;
                mallPageInfo = sellerGoodsService.tmpPageListAllSystemGoods(pageNum, pageSize, "0", "0");
                pageList = mallPageInfo.getElements();
            } catch (Exception e) {
                logger.error("---> 遍历识别商品图片有效性的处理报错:", e);
            }
        }

        Map<String, Object> retData = new HashMap<>();
        retData.put("invalidImageGoodsLoopCount", invalidImageGoodsLoopCount);
        retData.put("invalidImageGoodsIds", invalidImageGoodsIds);
        logger.info("---> 遍历的系统商品数量为:{}, 有图片问题的商品id集合:{}", invalidImageGoodsLoopCount, JsonUtils.bean2Json(invalidImageGoodsIds));
        resultObject.setData(retData);
        return resultObject;
    }

    @GetMapping(action + "invalidImageGoods.get")
    public Object getInvalidImageGoods(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();

        Map<String, Object> retData = new HashMap<>();
        retData.put("version", "2023-12-30");
        retData.put("invalidImageGoodsLoopCount", invalidImageGoodsLoopCount);
        retData.put("invalidImageGoodsIds", JsonUtils.bean2Json(invalidImageGoodsIds));
        resultObject.setData(retData);
        return resultObject;
    }

    private boolean checkFileExist(String imageUrl) {
        if (StrUtil.isBlank(imageUrl)) {
            logger.warn("---> DemoController.checkFileExist 图片地址不存在");
            return false;
        }
        if (!imageUrl.trim().startsWith("http")) {
            logger.warn("---> DemoController.checkFileExist 图片地址不规范:{}", imageUrl);
            return false;
        }

        // 经观察，商品图片地址都包含 http 前缀
        try {
            URL url = new URL(imageUrl);
            URLConnection conn = url.openConnection();

            String contentType = conn.getHeaderField("Content-Type");
            if (StrUtil.isBlank(contentType)) {
                // 暂时返回 true
                return true;
            }
            if (contentType.contains("application/xml")) {
                logger.warn("---> DemoController.checkFileExist 图片地址探测返回格式不是图片类型:{}", imageUrl);
                return false;
            }

            return true;
        } catch (Exception e) {
            logger.error("---> DemoController.checkFileExist 图片地址探测报错:{} \n", imageUrl, e);
            return false;
        }
    }
}
