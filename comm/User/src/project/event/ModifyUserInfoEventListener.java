package project.event;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import kernel.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import project.event.message.ModifyUserInfoEvent;
import project.event.model.UserChangeInfo;
import project.mall.evaluation.EvaluationService;
import project.mall.goods.model.Evaluation;
import project.mall.utils.MallPageInfo;
import project.party.PartyService;
import project.redis.RedisHandler;
import project.user.UserService;
import project.user.internal.OnlineUserService;
import project.user.token.TokenService;

import java.util.List;

/**
 * 用户修改了关键信息后，需要同步相关的业务数据
 * 目前可见受影响的业务数据：
 * 1. 订单评论记录里冗余存储了 username 字段值
 * 2. ....
 *
 */
public class ModifyUserInfoEventListener implements ApplicationListener<ModifyUserInfoEvent> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private RedisHandler redisHandler;

    private PartyService partyService;

    private UserService userService;

    private OnlineUserService onlineUserService;

    private TokenService tokenService;

    private EvaluationService evaluationService;

    @Override
    public void onApplicationEvent(ModifyUserInfoEvent event) {
        UserChangeInfo changeInfo = event.getChangeInfo();
        logger.info("监听到用户修改了关键信息" + JsonUtils.getJsonString(changeInfo));

        try {
            String oldUserName = changeInfo.getOldUserName();
            String newUserName = changeInfo.getNewUserName();
            if (oldUserName == null) {
                oldUserName = "";
            }
            if (newUserName == null) {
                newUserName = "";
            }

            userService.updateSyncUserInfo(changeInfo);
            if (!oldUserName.equals(newUserName)) {
                // 用户账号发生了改变，删除 token 强制用户重新登录
                onlineUserService.del(changeInfo.getPartyId());
                tokenService.removeLoginToken(changeInfo.getPartyId());
            }
        } catch (Exception e) {
            logger.error("用户修改个人关键信息后，同步修改用户记录报错，用户变更信息为: " + JsonUtils.getJsonString(changeInfo), e);
        }

        try {
            syncEvaluation(changeInfo);
        } catch (Exception e) {
            logger.error("用户修改个人关键信息后，同步修改订单评论记录处理报错，用户变更信息为: " + JsonUtils.getJsonString(changeInfo), e);
        }
    }

    /**
     * 同步修改订单评论记录里的 userName 字段值
     *
     * @param changeInfo
     */
    private void syncEvaluation(UserChangeInfo changeInfo) {
        String oldUserName = changeInfo.getOldUserName();
        String newUserName = changeInfo.getNewUserName();
        if (oldUserName == null) {
            oldUserName = "";
        }
        if (newUserName == null) {
            newUserName = "";
        }
        if (StrUtil.isBlank(oldUserName) && StrUtil.isBlank(newUserName)) {
            // 用户账号没变过
            return;
        }
        if (oldUserName.equals(newUserName)) {
            // 用户账号没变过
            return;
        }

        logger.info("用户修改了账号值，准备同步修改订单评论记录中的 uerName 字段值... 原始值:" + oldUserName + ", 新值:" + newUserName);
        // 目前的业务处理中，订单评论记录里， userName 字段必定有值
        // 用户账号有变更，检查是否存在该用户的评论
        if (StrUtil.isNotBlank(oldUserName) && !oldUserName.equals("0")) {
            int pageNum = 1;
            int pageSize = 100;
            String sellerGoodsId = "";
            String userName = oldUserName;
            String evaluationType = "0";

            while (true) {
                MallPageInfo pageInfo = evaluationService.listEvaluations(pageNum, pageSize, sellerGoodsId, userName, evaluationType);
                pageNum++;

                List<Evaluation> pageList = (List<Evaluation>) pageInfo.getElements();
                if (CollectionUtil.isEmpty(pageList)) {
                    break;
                }
                for (Evaluation oneEntity : pageList) {
                    oneEntity.setUserName(newUserName);

                    evaluationService.updateEvaluation(oneEntity);
                }

            }
        }

        logger.info("用户修改了账号值:" + oldUserName + " ===> " + newUserName + "，完成订单评论记录中的 uerName 字段值的同步修改");
    }

    public void setRedisHandler(RedisHandler redisHandler) {
        this.redisHandler = redisHandler;
    }

    public void setPartyService(PartyService partyService) {
        this.partyService = partyService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setOnlineUserService(OnlineUserService onlineUserService) {
        this.onlineUserService = onlineUserService;
    }

    public void setTokenService(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    public void setEvaluationService(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

}
