package kernel.constants;

/**
 * 统一放置 localCache 的 bucket 名称，放置 key 冲突，便于维护
 */
public interface LocalCacheBucketKey {
    //
    String GoodsSkuAttrListCache = "goodsSkuAttrListCache";

    //
    String GoodsAttrValueLangCache = "goodsAttrValueLangCache";

    //
    String SellerGoodsSkuCache = "sellerGoodsSkuCache";

    //
    String GoodsAttrListBySkuCache = "goodsAttrListBySkuCache";


    String CountLoginByDay = "CountLoginByDay";

    String CountRegisterByDay = "CountRegisterByDay";

    String CountRegisterSellerByDay = "CountRegisterSellerByDay";

    String CountOrderByDay = "CountOrderByDay";

    String CountAllUser = "CountAllUser";

    String CountAllSeller = "CountAllSeller";

    String TotalProfitByDay = "TotalProfitByDay";

    String SumWithdrawByDay = "SumWithdrawByDay";

    String SumRechargeByDay = "SumRechargeByDay";

    String NewRechargeByDay = "NewRechargeByDay";

    String NewWithdrawByDay = "NewWithdrawByDay";

    String CountWithdrawByDay  = "CountWithdrawByDay";

    String CountRechargeByDay = "CountRechargeByDay";

    String SumSellerOrdersPrize = "SumSellerOrdersPrize";

}
