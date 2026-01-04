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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ai.houyi.zhuque.commons.page.Page;
import ai.houyi.zhuque.commons.web.IController;
import ai.houyi.zhuque.core.model.query.AuditAdvertiserQueryReq;
import ai.houyi.zhuque.core.service.AuditAdvertiserService;
import ai.houyi.zhuque.dao.model.AdvertiserQualification;
import ai.houyi.zhuque.dao.model.AuditAdvertiser;
import io.swagger.annotations.Api;

/**
 * @author weiping wang
 */
@RestController
@RequestMapping("/audit-advertisers")
@Api(tags = "审核广告主管理")
public class AuditAdvertiserController implements IController<AuditAdvertiser, AuditAdvertiserQueryReq, Integer> {
	@Autowired
	private AuditAdvertiserService auditAdvertiserService;

	@PostMapping
	public void saveOrUpdate(AuditAdvertiser t) {
		if (t.getId() == null) {
			auditAdvertiserService.save(t);
		} else {
			auditAdvertiserService.update(t);
		}
	}

	@DeleteMapping("/{id}")
	public void deleteById(@PathVariable Integer id) {
		auditAdvertiserService.deleteById(id);
	}

	@GetMapping("/{id}")
	public AuditAdvertiser loadById(@PathVariable Integer id) {
		return auditAdvertiserService.loadById(id);
	}

	@PostMapping("/list")
	public Page<AuditAdvertiser> selectPage(AuditAdvertiserQueryReq queryReq) {
		return auditAdvertiserService.selectPageList(queryReq);
	}

	@GetMapping("/qualifications/{advertiserId}")
	public List<AdvertiserQualification> getAdvertiserQualifications(@PathVariable Integer advertiserId) {
		return auditAdvertiserService.getAdvertiserQualificationsByAdvertiserId(advertiserId);
	}

	@PostMapping("/qualifications")
	public void addAdvertiserQualification(AdvertiserQualification qualification) {
		auditAdvertiserService.addAdvertiserQualification(qualification);
	}

	@DeleteMapping("/qualifications/{qualificationId}")
	public void deleteAdvertiserQualification(@PathVariable Integer qualificationId) {
		auditAdvertiserService.deleteAdvertiserQualification(qualificationId);
	}
}
