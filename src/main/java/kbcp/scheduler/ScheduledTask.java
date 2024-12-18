package kbcp.scheduler;

import java.util.Date;

import kbcp.common.util.StringUtil;
import kbcp.common.util.VoUtil;
import kbcp.scheduler.vo.RunCntVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import lombok.extern.slf4j.Slf4j;
import kbcp.common.util.EstimateUtil;
import kbcp.common.util.LogUtil;

@Slf4j(topic = "batchLog")
@Configuration
@EnableScheduling
public class ScheduledTask {

	@Autowired
	private ScheduledTaskService service;
	
	@Bean
	public TaskScheduler taskScheduler() {
		log.debug("configureTasks : {}", new Date());
		
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(2);
        threadPoolTaskScheduler.setThreadGroupName("kbcp/batch");
        threadPoolTaskScheduler.setThreadNamePrefix("batch-thread-");
        threadPoolTaskScheduler.initialize();
        return threadPoolTaskScheduler;
	}

	// 로그폴더 정리
	@Scheduled(cron="${app.scheduler.cron.clean-logfolder}")
	public void cleanLogFolder() {
		EstimateUtil eu = new EstimateUtil();
		
		try {
			eu.setCheckPoint("logfolder.start");
			service.cleanLogFolder();
			eu.setCheckPoint("logfolder.finish");
		} catch (Exception e) {
			LogUtil.logException(e);
		} finally {
			log.info("cleanLogFolder : [{}]", eu.estimate("logfolder.start", "logfolder.finish"));
		}

	}

	// 당첨권 기간만료 쿠폰 발급
	@Scheduled(cron="${app.scheduler.cron.issue-coupon}")
	public void issueCoupon() {
		String sysProp = System.getProperty("batch.issueCoupon");
		if(!"ON".equalsIgnoreCase(sysProp) ) {
			return;
		}

		EstimateUtil eu = new EstimateUtil();
		RunCntVO runCntVO = new RunCntVO();

		sysProp = System.getProperty("svr.nm");
		if(StringUtil.isNull(sysProp)) {
			sysProp = "unknownSvr";
		}

		try {
			eu.setCheckPoint("issueCoupon.start");
			int nRet = service.issueCoupon(runCntVO);
			eu.setCheckPoint("issueCoupon.finish");
		} catch (Exception e) {
			LogUtil.logException(e);
		} finally {
			log.info("[{}] issueCoupon : {}ms elapsed.",
					sysProp, eu.estimate("issueCoupon.start", "issueCoupon.finish"));
			log.info("[{}] issueCoupon end.\n{}",
					sysProp, VoUtil.toJson(runCntVO));
		}
	}

}
