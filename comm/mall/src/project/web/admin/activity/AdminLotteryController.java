//package project.web.admin.activity;
//
//import kernel.util.PageInfo;
//import kernel.web.Page;
//import kernel.web.PageActionSupport;
//import kernel.web.ResultObject;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RestController;
//import project.mall.activity.service.lottery.LotteryService;
//import project.mall.activity.dto.lottery.LotteryDTO;
//import project.web.admin.dto.LotteryQueryDto;
//
//import javax.annotation.Resource;
//import javax.servlet.http.HttpServletRequest;
//
//@RestController
//@CrossOrigin
//public class AdminLotteryController extends PageActionSupport {
//
//    @Resource
//    private LotteryService lotteryService;
//
//    private final String action = "/lottery";
//
//    @PostMapping(action + "/pageList.action")
//    public ResultObject pageList(HttpServletRequest request, LotteryQueryDto queryDto) {
//
//        ResultObject resultObject = new ResultObject();
//        PageInfo pageInfo = getPageInfo(request);
//
//        Page page = lotteryService.paged(queryDto.getName(), queryDto.getStatus(), queryDto.getStartTime(), queryDto.getEndTime(), pageInfo.getPageNum(), pageInfo.getPageSize());
//        resultObject.setData(page);
//        return resultObject;
//    }
//
//
//    @PostMapping(action + "/add.action")
//    public ResultObject add(LotteryDTO lotteryDTO) {
//
//        ResultObject resultObject = new ResultObject();
//        lotteryService.add(lotteryDTO);
//        return resultObject;
//    }
//
//
//    @PostMapping(action + "/delete.action")
//    public ResultObject delete(String id) {
//
//        ResultObject resultObject = new ResultObject();
//        lotteryService.delete(id);
//        return resultObject;
//    }
//
//    @PostMapping(action + "/update.action")
//    public Object update(LotteryDTO lotteryDTO) {
//
//        ResultObject resultObject = new ResultObject();
//        lotteryService.update(lotteryDTO);
//        return resultObject;
//    }
//
//
//    @PostMapping(action + "/detail.action")
//    public Object detail(String id) {
//
//        ResultObject resultObject = new ResultObject();
//        LotteryDTO detail = lotteryService.detail(id);
//        resultObject.setData(detail);
//        return resultObject;
//    }
//}
