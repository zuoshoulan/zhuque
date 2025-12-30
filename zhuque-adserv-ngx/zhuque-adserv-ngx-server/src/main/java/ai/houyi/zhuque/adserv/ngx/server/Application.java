/*
 * Copyright 2017-2019 The OpenAds Project
 *
 * The OpenAds Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package ai.houyi.zhuque.adserv.ngx.server;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author weiping wang
 */
@SpringBootApplication
@ComponentScan(basePackages = {"ai.houyi.zhuque.adserv.ngx.service"})
public class Application {
	private static final int DEFAULT_PORT = 16888;

	public static void main(String[] args) throws Exception {
		int port = DEFAULT_PORT;
		if (args.length > 0) {
			port = NumberUtils.toInt(args[0], DEFAULT_PORT);
		}

		// 设置默认端口
		System.setProperty("server.port", String.valueOf(port));

		SpringApplication.run(Application.class, args);
	}
}
