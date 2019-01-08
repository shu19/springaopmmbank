package com.cg.app.account.service.aspect;

import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.cg.app.account.SavingsAccount;
import com.cg.app.exception.InsufficientFundsException;

@Aspect
@Component
public class ValidationAspect {

	Logger logger = Logger.getLogger(ValidationAspect.class);

	@Around("execution(* com.cg.app.account.service.SavingsAccountServiceImpl.deposit(..))")
	public void deposit(ProceedingJoinPoint pjp) throws Throwable {
		logger.info("In Deposit method");
		Object args[] = pjp.getArgs();

		Double amount = (Double) args[1];
		if (amount > 0) {
			pjp.proceed();
			logger.info("Deposit Success");
		} else {
			logger.info("Invalid Input Amount!");
		}

	}

	@Around("execution(* com.cg.app.account.service.SavingsAccountServiceImpl.withdraw(..))")
	public void withdraw(ProceedingJoinPoint pjp) throws Throwable {
		logger.info("In Withdraw method");
		Object args[] = pjp.getArgs();
		SavingsAccount account = (SavingsAccount) args[0];
		Double amount = (Double) args[1];
		
		if (account.getBankAccount().getAccountBalance() >= amount && amount > 0) {
			pjp.proceed();
			logger.info("Withdraw Success");
		} else {
			logger.info("Invalid Input or Insufficient Funds!");
			throw new InsufficientFundsException("Invalid Input or Insufficient Funds!");
		}
	}

	@AfterThrowing(pointcut = "execution(* com.cg.app.account.service.SavingsAccountServiceImpl.withdraw(..))", throwing = "error")
	public void exception(JoinPoint jp, Throwable error) {
		logger.error("@AfterThrowing : " + error);
		logger.info("Method Signature: " + jp.getSignature());
		logger.info("Exception: " + error);
	}
}
