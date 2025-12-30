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
package ai.houyi.zhuque.commons.web;

import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import ai.houyi.zhuque.commons.web.Response;

/**
 * 统一返回值处理器，将返回值包装为 Response 对象
 *
 * @author weiping wang
 */
public class ZhuqueMethodReturnValueHandler implements HandlerMethodReturnValueHandler {

	@Override
	public boolean supportsReturnType(MethodParameter returnType) {
		// 如果返回类型已经是 Response，则不需要处理
		return !Response.class.isAssignableFrom(returnType.getParameterType());
	}

	@Override
	public void handleReturnValue(Object value, MethodParameter returnType,
			ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
		// 将返回值包装为 Response
		Response response = new Response(0, "ok", value);

		// 使用 Spring MVC 的默认处理器处理 Response 对象
		mavContainer.setRequestHandled(false);
	}
}
