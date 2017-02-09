package com.sun.rooster.test;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.DockerCmdExecFactory;
import com.github.dockerjava.api.command.ListContainersCmd;
import com.github.dockerjava.api.command.ListImagesCmd;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.Info;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.jaxrs.JerseyDockerCmdExecFactory;

import java.util.List;

/**
 * Created by Shibo on 17/1/5.
 */
public class Test {

    public static void main(String[] args) {
        DockerClient dockerClient = new Test().init();
        Info exec = dockerClient.infoCmd().exec();
        System.out.println(exec);


        List<Container> exec1 = dockerClient.listContainersCmd().exec();
        for (Container container : exec1) {
            System.out.println(container.getId());
        }


    }

    public DockerClient init () {
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost("tcp://sunshibo.com:2375")
//                .withDockerTlsVerify(true)
                .withDockerCertPath("/home/user/.docker")
//                .withRegistryUsername(registryUser)
//                .withRegistryPassword(registryPass)
//                .withRegistryEmail(registryMail)
//                .withRegistryUrl(registryUrl)
                .build();

// using jaxrs/jersey implementation here (netty impl is also available)
        DockerCmdExecFactory dockerCmdExecFactory = new JerseyDockerCmdExecFactory()
                .withReadTimeout(1000)
                .withConnectTimeout(1000)
                .withMaxTotalConnections(100)
                .withMaxPerRouteConnections(10);

        DockerClient dockerClient = DockerClientBuilder.getInstance(config)
                .withDockerCmdExecFactory(dockerCmdExecFactory)
                .build();
        return dockerClient;
    }
}
