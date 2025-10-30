package com.scheduletool.service;

import com.scheduletool.model.Category;
import com.scheduletool.model.League;
import com.scheduletool.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CategoryService {
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
    
    public Optional<Category> getCategoryById(Integer id) {
        return categoryRepository.findById(id);
    }
    
    public List<Category> getCategoriesByLeague(League league) {
        return categoryRepository.findByLeague(league);
    }

    public List<Category> getCategoriesByLeagueId(Integer leagueId) {
        return categoryRepository.findByLeagueId(leagueId);
    }

    public List<Category> getUpcomingCategoriesByLeagueId(Integer leagueId) {
        return categoryRepository.findByLeagueIdAndDateGreaterThanEqual(leagueId, LocalDate.now());
    }

    public List<Category> getCategoriesByDate(LocalDate date) {
        return categoryRepository.findByDate(date);
    }
    
    public List<Category> getCategoriesByLeagueAndDate(League league, LocalDate date) {
        return categoryRepository.findByLeagueAndDate(league, date);
    }
    
    public List<Category> getCategoriesByDateRange(LocalDate startDate, LocalDate endDate) {
        return categoryRepository.findByDateBetween(startDate, endDate);
    }
    
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }
    
    public Category updateCategory(Integer id, Category categoryDetails) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        
        category.setLeague(categoryDetails.getLeague());
        category.setDate(categoryDetails.getDate());
        category.setEndDate(categoryDetails.getEndDate());
        category.setHeader(categoryDetails.getHeader());
        category.setExclude(categoryDetails.getExclude());
        category.setOverride(categoryDetails.getOverride());
        
        return categoryRepository.save(category);
    }
    
    public void deleteCategory(Integer id) {
        categoryRepository.deleteById(id);
    }
}

