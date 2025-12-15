package project.web.admin.activity;

import kernel.exception.BusinessException;
import kernel.util.PageInfo;
import kernel.util.StringUtils;
import kernel.web.BaseAction;
import kernel.web.Page;
import kernel.web.ResultObject;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import project.mall.activity.dto.lottery.LotteryPrizeDTO;
import project.mall.activity.dto.lottery.LotteryReceivePrizeDTO;
import project.mall.activity.model.ActivityPrize;
import project.mall.activity.model.lottery.LotteryReceive;
import project.mall.activity.service.ActivityPrizeService;
import project.mall.activity.service.lottery.LotteryReceiveService;
import project.party.PartyService;
import project.party.model.Party;
import project.tip.TipService;
import project.web.admin.dto.LotteryReceiveQueryDto;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;


@RestController
@CrossOrigin
public class AdminLotteryReceiveController extends BaseAction {

    private final String action = "/lotteryReceive";

    @Resource
    private LotteryReceiveService lotteryReceiveService;

    @Resource
    private ActivityPrizeService activityPrizeService;

    @Resource
    private PartyService partyService;

    @Resource
    private TipService tipService;


    @PostMapping(action + "/pageList.action")
    public ResultObject pageList(HttpServletRequest request, LotteryReceiveQueryDto queryDto) {
        ResultObject resultObject = new ResultObject();
        PageInfo pageInfo = getPageInfo(request);

        Page page = lotteryReceiveService.paged(queryDto.getUsername(), queryDto.getUid(), queryDto.getSellerName(),
                queryDto.getState(), queryDto.getPrizeType(), queryDto.getStartTime(), queryDto.getEndTime(), pageInfo.getPageNum(), pageInfo.getPageSize());
        resultObject.setData(page);
        return resultObject;
    }


    @PostMapping(action + "/delete.action")
    public ResultObject delete(String id) {
        ResultObject resultObject = new ResultObject();
        lotteryReceiveService.delete(id);
        return resultObject;
    }

    /**
     * 查询派发奖品信息
     *
     * @param id
     * @return
     */
    @PostMapping(action + "/getIssues.action")
    public ResultObject getIssues(String id) {

        ResultObject resultObject = new ResultObject();

        LotteryReceive receive = lotteryReceiveService.detail(id);
        String prizeIds = receive.getPrizeIds();
        List<String> prizeIdList = Arrays.asList(prizeIds.split(","));

        Map<String, Long> numbers = prizeIdList.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        List<ActivityPrize> lotteryPrizes = activityPrizeService.listByIds(prizeIdList);

        LotteryReceivePrizeDTO receivePrizeDTO = new LotteryReceivePrizeDTO();

        receivePrizeDTO.setId(id);
        Party party = partyService.getById(receive.getPartyId());
        if (Objects.nonNull(party)) {
            receivePrizeDTO.setPartyId(party.getUsercode());
        }
        receivePrizeDTO.setSellerName(receive.getSellerName());
        receivePrizeDTO.setPrizeType(receive.getPrizeType());
        receivePrizeDTO.setRemark(receive.getRemark());
        receivePrizeDTO.setPrizeAmount(receive.getPrizeAmount());

        if (CollectionUtils.isNotEmpty(lotteryPrizes)) {
            List<LotteryPrizeDTO> prizeDTOList = new ArrayList<>();
            for (ActivityPrize prize : lotteryPrizes) {
                LotteryPrizeDTO lotteryPrizeDTO = new LotteryPrizeDTO();
                lotteryPrizeDTO.setId(prize.getId().toString());
                lotteryPrizeDTO.setPrizeAmount(prize.getPrizeAmount());
                if (StringUtils.isNotEmpty(prize.getPrizeNameCn())) {
                    lotteryPrizeDTO.setPrizeName(prize.getPrizeNameCn());
                } else {
                    lotteryPrizeDTO.setPrizeName(prize.getPrizeNameEn());
                }
                Long num = numbers.get(prize.getId().toString());
                if (Objects.nonNull(num)) {
                    lotteryPrizeDTO.setNum(num.intValue());
                } else {
                    lotteryPrizeDTO.setNum(0);
                }
                prizeDTOList.add(lotteryPrizeDTO);
            }
            receivePrizeDTO.setPrizeDTOList(prizeDTOList);
        }
        resultObject.setData(receivePrizeDTO);
        return resultObject;
    }

    /**
     * 派发奖品
     *
     * @param id
     * @return
     */
    @PostMapping(action + "/issue.action")
    public ResultObject issue(String id, String remark) {
        ResultObject resultObject = new ResultObject();

        LotteryReceive receive = lotteryReceiveService.detail(id);
        if (receive == null || receive.getState() == 1) {
            resultObject.setCode("-1");
            resultObject.setMsg("已派发");
            return resultObject;
        }

        if (receive.getPrizeType() == 2) {
            // 彩金类奖品，先给用户加彩金
            lotteryReceiveService.updatePayout(receive.getPartyId(), receive.getPrizeAmount().doubleValue());
        }

        receive.setState(1);
        receive.setUpdateTime(new Date());
        receive.setIssueTime(new Date());
        if (StringUtils.isNotEmpty(remark)) {
            receive.setRemark(remark);
        }
        lotteryReceiveService.update(receive);

        tipService.deleteTip(receive.getId().toString());
        resultObject.setMsg("success");
        return resultObject;
    }

    @PostMapping(action + "/detail.action")
    public ResultObject detail(String id) {
        ResultObject resultObject = new ResultObject();
        LotteryReceive detail = lotteryReceiveService.detail(id);
        resultObject.setData(detail);
        return resultObject;
    }
}
