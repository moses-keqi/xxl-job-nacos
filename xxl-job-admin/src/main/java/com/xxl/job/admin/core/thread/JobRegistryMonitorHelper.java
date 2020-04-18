package com.xxl.job.admin.core.thread;

import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.core.enums.RegistryConfig;
import com.xxl.job.core.util.RestTemplateClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * job registry instance
 *
 * @author xuxueli 2016-10-02 19:10:24
 * @author HanKeQi
 * @Description
 * @date 2020/4/13 3:01 PM
 **/
public class JobRegistryMonitorHelper {
    private static Logger logger = LoggerFactory.getLogger(JobRegistryMonitorHelper.class);

    private static JobRegistryMonitorHelper instance = new JobRegistryMonitorHelper();

    public static JobRegistryMonitorHelper getInstance() {
        return instance;
    }

    private static ExecutorService executorService = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
            Runtime.getRuntime().availableProcessors(), 10L, TimeUnit.SECONDS, new LinkedBlockingDeque<>(200), (Runnable r) -> new Thread(r, "notice_thread_pool")
    );

    private Thread registryThread;
    private volatile boolean toStop = false;

    public void start() {
        registryThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!toStop) {
                    try {
                        // auto registry group
                        List<XxlJobGroup> groupList = XxlJobAdminConfig.getAdminConfig().getXxlJobGroupDao().findByAddressType(0);
                        groupList.forEach(xxlJobGroup -> {
                            // 一个是admin一个是executor执行器类型
                            XxlJobAdminConfig.getAdminConfig().xxlJobService().registryByDiscovery(xxlJobGroup, RegistryConfig.RegistType.EXECUTOR.name());
                        });
                        if (!CollectionUtils.isEmpty(groupList)) {
                            // remove dead address (admin/executor) 删除90秒内无更新的注册列表
                            List<Integer> ids = XxlJobAdminConfig.getAdminConfig().getXxlJobRegistryDao().findDead(RegistryConfig.DEAD_TIMEOUT);
                            if (!CollectionUtils.isEmpty(ids)) {
                                XxlJobAdminConfig.getAdminConfig().getXxlJobRegistryDao().removeDead(ids);
                            }
                        }
                        List<XxlJobGroup> noticeList = XxlJobAdminConfig.getAdminConfig().getXxlJobGroupDao().findByAddressType(0);
                        noticeList.forEach(xxlJobGroup -> {
                            //TODO 重构 By HanKeyQi
                            String appName = xxlJobGroup.getAppName();
                            executorService.submit(() -> {
                                try {
//                                    Map<String, String> map = new HashMap<>(1);
//                                    map.put("adminName", XxlJobAdminConfig.getAdminConfig().getName());
                                    //RestTemplateClient.getPost("http://" + appName + RestTemplateClient.adminList, map);

                                    RestTemplateClient.getPost("http://" + appName + RestTemplateClient.adminList + "?adminName=" + XxlJobAdminConfig.getAdminConfig().getName(), null);
                                } catch (Exception e) {
                                    logger.error("notice app error : {}, url address : {}", e.toString(), appName);
                                }
                            });
                        });
                    } catch (Exception e) {
                        if (!toStop) {
                            logger.error(">>>>>>>>>>> xxl-job, job registry monitor thread error:{}", e);
                        }
                    }
                    try {
                        TimeUnit.SECONDS.sleep(RegistryConfig.BEAT_TIMEOUT);
                    } catch (InterruptedException e) {
                        if (!toStop) {
                            logger.error(">>>>>>>>>>> xxl-job, job registry monitor thread error:{}", e);
                        }
                    }
                }
                logger.info(">>>>>>>>>>> xxl-job, job registry monitor thread stop");
            }
        });
        registryThread.setDaemon(true);
        registryThread.setName("xxl-job, admin JobRegistryMonitorHelper");
        registryThread.start();
    }

    public void toStop() {
        toStop = true;
        // interrupt and wait
        registryThread.interrupt();
        try {
            registryThread.join();
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
