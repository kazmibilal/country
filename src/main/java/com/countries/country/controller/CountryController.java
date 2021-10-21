package com.countries.country.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.countries.country.model.Country;

import ch.qos.logback.core.recovery.ResilientSyslogOutputStream;

@RestController
@ConfigurationProperties(prefix="endpoint")
@RequestMapping("/countryApp")
public class CountryController {
	
	@Value("${countries.url}")
	private String baseUrl;

	@Autowired
	private RestTemplate restTemplate;
	
	List<Country> allCountries = new ArrayList<Country>();

	
//	Get All Countries
	@GetMapping("/countries")
	public List<Country> getCountries() {
		Country[] responseEntity = restTemplate.getForObject(baseUrl, Country[].class);
		allCountries = Arrays.asList(responseEntity);
		return allCountries;
	}
	
	
//	Filter Countries By Region
	@SuppressWarnings("null")
	@GetMapping("/filterCountriesByRegion")
	public List<Country> filterCountriesByRegion(@RequestParam String[] region) {
		
		String url = "https://restcountries.com/v2/all";
		Country[] countries = restTemplate.getForObject(url, Country[].class);
		List<Country> countriesList = Arrays.asList(countries);
		List finalList = new ArrayList<Country>();
		
		System.out.println("here");
			
			Arrays.stream(region).forEach(reg ->{
				
				System.out.println("CountryLIst :: "+countriesList);
				System.out.println("Reg :: "+reg);
				
			 List<Country> countriesByRegion = countriesList.stream()
						  .filter(countr -> reg.toLowerCase().equals(countr.getRegion().toLowerCase()))
						  .collect(Collectors.toList());
			 
			 System.out.println("countriesByRegion :: "+countriesByRegion);
				
				finalList.add(countriesByRegion);
			});
		
		return finalList;
	}
	
//	Search Country By Name
	@GetMapping("/searchCountryByName")
	public Country searchCountryByName(@RequestParam String name) {

		System.out.println("name :: " + name);
		String url = "https://restcountries.com/v2/all";
		Country[] countries = restTemplate.getForObject(url, Country[].class);
		List<Country> countriesList = Arrays.asList(countries);
		Country country = countriesList.stream()
				  .filter(countr -> name.toLowerCase().equals(countr.getName().toLowerCase()))
				  .findAny()
				  .orElse(null);
		
		System.out.println("Countryname :: " + country.getName());

		return country;
	}
	
	
	
//	Sort Countries By Population Or Area
	@GetMapping("/sortCountries")
	public List<Country> sortCountries(@RequestParam String sortColumn , @RequestParam String sortOrder) {
		
		String url = "https://restcountries.com/v2/all";
		Country[] countries = restTemplate.getForObject(url, Country[].class);
		List<Country> countriesList = Arrays.asList(countries);

		System.out.println("sortColumn :: " + sortColumn);
		System.out.println("sortOrder :: " + sortOrder);
		
		if(sortColumn.toLowerCase().equals("population") && sortOrder.toLowerCase().equals("asc")) {
		Collections.sort(countriesList, (o1, o2) -> (o1.getPopulation().compareTo(o2.getPopulation())));
		}
		else if(sortColumn.toLowerCase().equals("population") && sortOrder.toLowerCase().equals("desc")) {
			Collections.sort(countriesList, (o1, o2) -> (o2.getPopulation().compareTo(o1.getPopulation())));
		}
		
//		Using comparator to Sort By Area
		
		else if(sortColumn.toLowerCase().equals("area") && sortOrder.toLowerCase().equals("asc")) {
			countriesList.sort(Comparator.comparing(
				    Country::getArea, 
				    Comparator.nullsFirst(Comparator.naturalOrder())
				));
		}
		else if(sortColumn.toLowerCase().equals("area") && sortOrder.toLowerCase().equals("desc")) {
			countriesList.sort(Comparator.comparing(
				    Country::getArea, 
				    Comparator.nullsLast(Comparator.reverseOrder())
				));
		}
		
		
		return countriesList;
	}

}
