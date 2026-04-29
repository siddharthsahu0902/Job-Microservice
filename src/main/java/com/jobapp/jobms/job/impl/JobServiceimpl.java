package com.jobapp.jobms.job.impl;


import com.jobapp.jobms.job.Job;
import com.jobapp.jobms.job.JobRepository;
import com.jobapp.jobms.job.JobService;
import com.jobapp.jobms.job.clients.CompanyClient;
import com.jobapp.jobms.job.clients.ReviewClient;
import com.jobapp.jobms.job.dto.JobDTO;
import com.jobapp.jobms.job.external.Company;
import com.jobapp.jobms.job.external.Review;
import com.jobapp.jobms.job.mapper.JobMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class JobServiceimpl implements JobService {
//    private List<Job> jobs =  new ArrayList<Job>();
//    private static Long id = Long.valueOf(1);
    JobRepository jobRepository;

    int attempts=0;

    @Autowired
    RestTemplate restTemplate;

    private CompanyClient companyClient;
    private ReviewClient reviewClient;

    public JobServiceimpl(JobRepository jobRepository, CompanyClient companyClient, ReviewClient reviewClient) {
        this.companyClient = companyClient;
        this.jobRepository = jobRepository;
        this.reviewClient = reviewClient;
    }
    @Override
//    @CircuitBreaker(name = "companyBreaker", fallbackMethod = "companyBreakerFallback")
    @Retry(name = "companyBreaker", fallbackMethod = "companyBreakerFallback")
    public List<JobDTO> findAll() {
        System.out.println("companyBreaker attempt : "+ attempts);
        attempts++;
        List<Job> jobs = jobRepository.findAll();
        List<JobDTO> jobWithCompanyDTOS = new ArrayList<>();

//        RestTemplate restTemplate = new RestTemplate();

        for(Job job : jobs) {
            JobDTO jobDTO = convertToDTO(job);
            jobWithCompanyDTOS.add(jobDTO);
        }

        return jobWithCompanyDTOS;
    }
    public List<String> companyBreakerFallback(Exception e) {
        List<String> list = new ArrayList<>();
        list.add("Call to the service failed with the error : " + e.getMessage());
        return list;
    }
    private JobDTO convertToDTO(Job job) {
        Company company = companyClient.getCompany(job.getCompanyId());
        List<Review> reviews = reviewClient.getReviews(job.getCompanyId());

        JobDTO jobDTO;
//        Company company = restTemplate.getForObject("http://COMPANYMS:8081/companies/" + job.getCompanyId(), Company.class);
       // ResponseEntity<List<Review>> reviewResponse;
       // reviewResponse = restTemplate.exchange("http://REVIEWMS:8083/reviews?companyId=" + String.valueOf(job.getCompanyId()), HttpMethod.GET, null, new ParameterizedTypeReference<List<Review>>() {} );
        //List<Review> reviews = reviewResponse.getBody();

        jobDTO = JobMapper.mapJobToJobWithCompanyDTO(job, company, reviews);
        return jobDTO;
    }

    @Override
    public void createJob(@NonNull Job job) {
        jobRepository.save(job);
    }

    @Override
    public JobDTO getJobById(Long id) {
        Job job = jobRepository.findById(id).orElse(null);
        if(job == null) {
            return null;
        }
        return convertToDTO(job);
    }

    public boolean deleteJobById(Long id){
        try{
            jobRepository.deleteById(id);
            return true;
        } catch(EmptyResultDataAccessException e){
            return false;
        }
    }

    public boolean updateJob(Long id, Job updatedJob) {
        Optional<Job> jobOptional = jobRepository.findById(id);
        if(jobOptional.isPresent()){
            Job job =  jobOptional.get();
            job.setTitle(updatedJob.getTitle());
            job.setDescription(updatedJob.getDescription());
            job.setLocation(updatedJob.getLocation());
            job.setMaxSalary(updatedJob.getMaxSalary());
            job.setMinSalary(updatedJob.getMinSalary());
            job.setCompanyId(updatedJob.getCompanyId());
            jobRepository.save(job);
            return true;
        }
        return false;
    }
}
