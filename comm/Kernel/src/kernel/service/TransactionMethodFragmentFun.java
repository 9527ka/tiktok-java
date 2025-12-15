package kernel.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import util.concurrent.gofun.core.FunParams;

import java.util.function.Function;

@Service
public class TransactionMethodFragmentFun {
    // 不能有这个注解
    //@Transactional(propagation = Propagation.REQUIRED)
    public FunParams runInTransaction(FunParams inputParams, Function<FunParams, FunParams> fun) {
        return fun.apply(inputParams);
    }
}
