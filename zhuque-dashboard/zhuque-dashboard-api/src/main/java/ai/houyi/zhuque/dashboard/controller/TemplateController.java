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
package ai.houyi.zhuque.dashboard.controller;

import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import ai.houyi.zhuque.commons.page.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import ai.houyi.zhuque.commons.web.IController;
import org.springframework.web.bind.annotation.RequestMapping;
import ai.houyi.zhuque.core.model.query.TemplateQueryReq;
import org.springframework.web.bind.annotation.RequestMapping;
import ai.houyi.zhuque.core.service.TemplateService;
import org.springframework.web.bind.annotation.RequestMapping;
import ai.houyi.zhuque.dao.model.Template;
import org.springframework.web.bind.annotation.RequestMapping;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author weiping wang
 */
@RestController
@RequestMapping("/templates")
@Api(tags= {"广告位模板管理"})
public class TemplateController implements IController<Template, TemplateQueryReq, Integer> {
	@Autowired
	private TemplateService templateService;

	@PostMapping
	@RequestMapping
	public void saveOrUpdate(Template template) {
		if (template.getId() != null) {
			templateService.save(template);
		} else {
			templateService.update(template);
		}
	}

	@DeleteMapping
	@RequestMapping("/{templateId}")
	public void deleteById(Integer templateId) {
		templateService.softDeleteById(templateId);
	}

	@GetMapping
	@RequestMapping
	public void selectAll() {
		templateService.selectAll();
	}

	@GetMapping
	@RequestMapping("/{templateId}")
	public Template loadById(Integer templateId) {
		return templateService.loadById(templateId);
	}

	@GetMapping
	@RequestMapping("/{name}")
	public List<Template> selectByName(String name) {
		return templateService.selectByName(name);
	}

	@PostMapping
	@RequestMapping("/list")
	public Page<Template> selectPage(TemplateQueryReq queryReq) {
		return templateService.selectPageList(queryReq);
	}

}
