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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ai.houyi.zhuque.commons.page.Page;
import ai.houyi.zhuque.commons.web.IController;
import ai.houyi.zhuque.core.model.AuthContext;
import ai.houyi.zhuque.core.model.query.AdGroupQueryReq;
import ai.houyi.zhuque.core.service.AdGroupService;
import ai.houyi.zhuque.dao.model.AdGroup;
import io.swagger.annotations.Api;

/**
 * 广告组管理
 * 
 * @author weiping wang
 */
@RestController
@RequestMapping("/adgroups")
@Api(tags="广告组管理")
public class AdGroupController implements IController<AdGroup, AdGroupQueryReq, Integer> {
	@Autowired
	private AdGroupService adGroupService;

	@PostMapping
	public void saveOrUpdate(AdGroup t) {
		if (t.getId() == null) {
			adGroupService.save(t);
		} else {
			adGroupService.update(t);
		}
	}

	@DeleteMapping("/{id}")
	public void deleteById(@PathVariable Integer id) {
		adGroupService.softDeleteById(id);
	}

	@GetMapping("/{id}")
	public AdGroup loadById(@PathVariable Integer id) {
		return adGroupService.loadById(id);
	}

	@PostMapping("/list")
	public Page<AdGroup> selectPage(AdGroupQueryReq queryReq) {
		queryReq.setAdvertiserId(AuthContext.currentUser().getId());

		return adGroupService.selectPageList(queryReq);
	}
}
