package com.jobapp.jobms.job.mapper;

import com.jobapp.jobms.job.Job;
import com.jobapp.jobms.job.dto.JobDTO;
import com.jobapp.jobms.job.external.Company;
import com.jobapp.jobms.job.external.Review;

import java.util.List;

public class JobMapper {
    public static JobDTO mapJobToJobWithCompanyDTO(Job job, Company company, List<Review> reviews) {
        JobDTO jobDTO = new JobDTO();
        jobDTO.setId(job.getId());
        jobDTO.setTitle(job.getTitle());
        jobDTO.setDescription(job.getDescription());
        jobDTO.setMaxSalary(job.getMaxSalary());
        jobDTO.setMinSalary(job.getMinSalary());
        jobDTO.setCompany(company);
        jobDTO.setReview(reviews);
        return jobDTO;
   }
}
