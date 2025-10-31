package com.scheduletool.service;

import com.scheduletool.model.Category;
import com.scheduletool.model.League;
import com.scheduletool.repository.CategoryRepository;
import com.scheduletool.repository.LeagueRepository;
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

    @Autowired
    private LeagueRepository leagueRepository;
    
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
    
    public Optional<Category> getCategoryById(Integer id) {
        return categoryRepository.findById(id);
    }
    
    public List<Category> getCategoriesByLeague(League league) {
        return categoryRepository.findByLeagueOrderByDateAsc(league);
    }

    public List<Category> getCategoriesByLeagueId(Integer leagueId) {
        return categoryRepository.findByLeagueIdOrderByDateAsc(leagueId);
    }

    public List<Category> getUpcomingCategoriesByLeagueId(Integer leagueId) {
        return categoryRepository.findByLeagueIdAndDateGreaterThanEqualOrderByDateAsc(leagueId, LocalDate.now());
    }

    public List<Category> getCategoriesByLeagueIdAndDate(Integer leagueId, LocalDate date) {
        System.out.println("CategoryService: Getting categories for leagueId=" + leagueId + " and date>=" + date);
        System.out.println("CategoryService: Date parameter type: " + date.getClass().getName());
        System.out.println("CategoryService: Date parameter value: " + date.toString());

        List<Category> categories = categoryRepository.findByLeagueIdAndDateGreaterThanEqualOrderByDateAsc(leagueId, date);

        System.out.println("CategoryService: Found " + categories.size() + " categories");

        // Check if there are any categories with date = 2025-10-31
        List<Category> allCategories = categoryRepository.findByLeagueIdOrderByDateAsc(leagueId);
        System.out.println("CategoryService: Total categories for league " + leagueId + ": " + allCategories.size());
        long oct31Count = allCategories.stream()
            .filter(c -> c.getDate().equals(LocalDate.of(2025, 10, 31)))
            .count();
        System.out.println("CategoryService: Categories with date 2025-10-31: " + oct31Count);

        for (Category cat : categories) {
            System.out.println("  - Category ID=" + cat.getId() + ", date=" + cat.getDate() + ", header=" + cat.getHeader());
        }
        return categories;
    }

    public List<Category> getCategoriesByDate(LocalDate date) {
        return categoryRepository.findByDateOrderByDateAsc(date);
    }

    public List<Category> getCategoriesByLeagueAndDate(League league, LocalDate date) {
        return categoryRepository.findByLeagueAndDateOrderByDateAsc(league, date);
    }

    public List<Category> getCategoriesByDateRange(LocalDate startDate, LocalDate endDate) {
        return categoryRepository.findByDateBetweenOrderByDateAsc(startDate, endDate);
    }
    
    public Category createCategory(Category category) {
        System.out.println("CategoryService: Creating category with header=" + category.getHeader());
        System.out.println("CategoryService: League=" + category.getLeague());
        System.out.println("CategoryService: League ID=" + (category.getLeague() != null ? category.getLeague().getId() : "NULL"));

        // If league is provided but not fully loaded, fetch it from database
        if (category.getLeague() != null && category.getLeague().getId() != null) {
            League league = leagueRepository.findById(category.getLeague().getId())
                .orElseThrow(() -> new RuntimeException("League not found with id: " + category.getLeague().getId()));
            category.setLeague(league);
            System.out.println("CategoryService: Set league to: " + league.getName());
        }

        Category saved = categoryRepository.save(category);
        System.out.println("CategoryService: Saved category with ID=" + saved.getId() + ", league_id=" +
            (saved.getLeague() != null ? saved.getLeague().getId() : "NULL"));
        return saved;
    }
    
    public Category updateCategory(Integer id, Category categoryDetails) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

        category.setLeague(categoryDetails.getLeague());
        category.setDate(categoryDetails.getDate());
        category.setEndDate(categoryDetails.getEndDate());
        category.setHeader(categoryDetails.getHeader());
        category.setDescription(categoryDetails.getDescription());
        category.setEventGroupType(categoryDetails.getEventGroupType());
        category.setExclude(categoryDetails.getExclude());
        category.setOverride(categoryDetails.getOverride());

        return categoryRepository.save(category);
    }
    
    public void deleteCategory(Integer id) {
        categoryRepository.deleteById(id);
    }
}

