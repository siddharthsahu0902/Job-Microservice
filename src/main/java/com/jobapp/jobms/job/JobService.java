package com.jobapp.jobms.job;

import com.jobapp.jobms.job.dto.JobDTO;

import java.util.List;

public interface JobService {
    public List<JobDTO> findAll();
    public void createJob(Job job);

    public JobDTO getJobById(Long id);

    public boolean deleteJobById(Long id);
    public boolean updateJob(Long id, Job job);
}
