package project.web.admin.activity;

import kernel.util.PageInfo;
import kernel.web.BaseAction;
import kernel.web.Page;
import kernel.web.ResultObject;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import project.mall.activity.service.lottery.LotteryRecordService;
import project.mall.activity.model.lottery.LotteryRecord;
import project.web.admin.dto.LotteryRecordQueryDto;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin
public class AdminLotteryRecordController extends BaseAction {

    private final String action = "/lotteryRecord";

    @Resource
    private LotteryRecordService lotteryRecordService;

    @PostMapping(action + "/pageList.action")
    public Object pageList(HttpServletRequest request, LotteryRecordQueryDto queryDto) {

        ResultObject resultObject = new ResultObject();
        PageInfo pageInfo = getPageInfo(request);

        Page page = lotteryRecordService.paged(queryDto.getUsername(), queryDto.getUid(), queryDto.getSellerName(),
                queryDto.getReceiveState(), queryDto.getPrizeType(), queryDto.getStartTime(), queryDto.getEndTime(), pageInfo.getPageNum(), pageInfo.getPageSize());
        resultObject.setData(page);
        return resultObject;
    }


    @PostMapping(action + "/detail.action")
    public Object detail(String id) {

        ResultObject resultObject = new ResultObject();
        LotteryRecord detail = lotteryRecordService.detail(id);
        resultObject.setData(detail);
        return resultObject;
    }
}
