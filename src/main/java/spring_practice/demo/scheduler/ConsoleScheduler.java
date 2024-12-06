package spring_practice.demo.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class ConsoleScheduler {

    // 초 분 시 일 월 요일

    //1. 5초의 한번씩 로그 1을 찍는 스케줄러
    @Scheduled(cron = "0/5 * * * * *")
    public void schedulerWithFiveSeconds () {
        System.out.println("현재 시간 : " + new Date().toString());
        log.info("로그 1 : 5초의 한번씩 ");
    }

    //2. 5분의 한번씩 로그 2을 찍는 스케줄러
    @Scheduled(cron = "0 0/5 * * * *")
    public void schedulerWithFiveMinutes () {
        System.out.println("현재 시간 : " + new Date().toString());
        log.info("로그 2 : 5분의 한번씩 ");
    }

    //3. 매시 , 15분에 로그 3을 찍는 스케줄러
    @Scheduled(cron = "0 15 * * * *")
    public void schedulerWithEveryFifteenMinutes () {
        System.out.println("현재 시간 : " + new Date().toString());
        log.info("로그 3 : 매시, 15분에");
    }

    //4. 30분마다 로그 4을 찍는 스케줄러
    @Scheduled(cron = "0 0/30 * * * *")
    public void schedulerWithThirtyMinutes () {
        System.out.println("현재 시간 : " + new Date().toString());
        log.info("로그 4 : 30분마다");
    }

    //5. 매월 1일에 로그 5를 찍는 스케줄러
    @Scheduled(cron = "0 0 0 1 * *")
    public void schedulerWithOneDay () {
        System.out.println("현재 시간 : " + new Date().toString());
        log.info("로그 5 : 매월 1일에");
    }
}
