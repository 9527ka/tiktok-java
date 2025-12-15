//package project.web.admin.activity;
//
//import kernel.util.PageInfo;
//import kernel.web.BaseAction;
//import kernel.web.Page;
//import kernel.web.ResultObject;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RestController;
//import project.mall.activity.service.lottery.LotteryPrizeService;
//import project.mall.activity.model.lottery.LotteryPrize;
//import project.web.admin.dto.LotteryQueryDto;
//
//import javax.annotation.Resource;
//import javax.servlet.http.HttpServletRequest;
//import java.util.List;
//
//@RestController
//@CrossOrigin
//public class AdminLotteryPrizeController extends BaseAction {
//
//    @Resource
//    private LotteryPrizeService lotteryPrizeService;
//
//    private final String action = "/lotteryPrize";
//
//    @PostMapping(action + "/pageList.action")
//    public ResultObject pageList(HttpServletRequest request, LotteryQueryDto queryDto) {
//
//        ResultObject resultObject = new ResultObject();
//        PageInfo pageInfo = getPageInfo(request);
//        Page page = lotteryPrizeService.paged(queryDto.getName(), queryDto.getStatus(), queryDto.getStartTime(), queryDto.getEndTime(), pageInfo.getPageNum(), pageInfo.getPageSize());
//        resultObject.setData(page);
//        return resultObject;
//    }
//
//    @PostMapping(action + "/listAll.action")
//    public ResultObject list() {
//
//        ResultObject resultObject = new ResultObject();
//        List<LotteryPrize> results = lotteryPrizeService.listAll();
//        resultObject.setData(results);
//        return resultObject;
//    }
//
//
//    @PostMapping(action + "/add.action")
//    public ResultObject add(HttpServletRequest request, LotteryPrize lotteryPrize) {
//
//        ResultObject resultObject = new ResultObject();
//        String language = getLanguage(request);
//        lotteryPrizeService.add(lotteryPrize, language);
//        return resultObject;
//    }
//
//
//    @PostMapping(action + "/delete.action")
//    public ResultObject delete(String id) {
//
//        ResultObject resultObject = new ResultObject();
//        lotteryPrizeService.delete(id);
//        return resultObject;
//    }
//
//    @PostMapping(action + "/update.action")
//    public ResultObject update(HttpServletRequest request, LotteryPrize lotteryPrize) {
//
//        ResultObject resultObject = new ResultObject();
//        String language = getLanguage(request);
//        lotteryPrizeService.update(lotteryPrize, language);
//        return resultObject;
//    }
//
//
//    @PostMapping(action + "/detail.action")
//    public ResultObject detail(String id) {
//        ResultObject resultObject = new ResultObject();
//        LotteryPrize detail = lotteryPrizeService.detail(id);
//        resultObject.setData(detail);
//        return resultObject;
//    }
//}
