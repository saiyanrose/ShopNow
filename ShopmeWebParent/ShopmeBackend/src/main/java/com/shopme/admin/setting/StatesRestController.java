package com.shopme.admin.setting;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.shopme.common.entity.StateDTO;
import com.shopme.common.entity.States;

@RestController
public class StatesRestController {

	@Autowired
	private StateRepository stateRepository;

	@GetMapping("/states/list")
	public List<States> allStates() {
		return stateRepository.findAllByOrderByNameAsc();
	}

	@GetMapping("/states/{id}")
	public List<StateDTO> statesById(@PathVariable("id") Integer countryId) {
		List<States> listStates = stateRepository.findByCountryId(countryId);
		List<StateDTO> result = new ArrayList<StateDTO>();
		for (States states : listStates) {
			result.add(new StateDTO(states.getId(), states.getName()));
		}
		return result;
	}
	
	@PostMapping("/states/save")
	public String saveStates(@RequestBody States states) {
		States saveStates=stateRepository.save(states);
		return String.valueOf(saveStates.getId());
	}
	
	@GetMapping("/states/delete/{id}")
	public void deleteState(@PathVariable("id") Integer id) {
		stateRepository.deleteById(id);
	}
	

}
