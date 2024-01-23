package com.shopme.admin.user;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.admin.category.CategoryPageInfo;
import com.shopme.admin.category.CategoryRepository;
import com.shopme.admin.exceptions.CategoryNotFoundException;
import com.shopme.admin.exceptions.NoCategoryException;
import com.shopme.common.entity.Category;

@Service
@Transactional
public class CategoryService {
	
	private static final int Category_per_page = 1;

	@Autowired
	private CategoryRepository categoryRepository;

	public List<Category> findAllCategory(CategoryPageInfo categoryPageInfo, int pageNum, String sortDir,
			String keyword, String sortField) {		
		Sort sort = Sort.by(sortField);
		
		sort=sortDir.equals("asc") ? sort.ascending() : sort.descending();
		
		Pageable pageable = PageRequest.of(pageNum - 1, Category_per_page, sort);
		
		Page<Category> rootCategories=null;
		
		if (keyword != null && !keyword.isEmpty()) {
			rootCategories = categoryRepository.search(keyword,pageable);
		}else {
			rootCategories = categoryRepository.listRootCategories(pageable);
		}
		
		List<Category> pageContent = rootCategories.getContent()
										.parallelStream().collect(Collectors.toList());
		
		categoryPageInfo.setTotalElements(rootCategories.getTotalElements());
		
		categoryPageInfo.setTotalPages(rootCategories.getTotalPages());
		
		return hierarchialCategories(pageContent, sortDir);			
	}

	private List<Category> hierarchialCategories(List<Category> rootCategories, String sortDir) {
		
		List<Category> hierarchailCategories = new ArrayList<Category>();	
		
		rootCategories.parallelStream().forEach(c -> {
			hierarchailCategories.add(Category.full(c));

		    Set<Category> childrenCategories = sortSubCategory(c.getChildren(), sortDir);

		    childrenCategories.parallelStream().forEach(subcategory -> {
		        String name = "--" + subcategory.getName();
		        hierarchailCategories.add(Category.full(subcategory, name));
		        subhirarichal(hierarchailCategories, subcategory, 1, sortDir);
		    });
		});
		
		/*
		 * for (Category c : rootCategories) {
		 * hierarchailCategories.add(Category.full(c));
		 * 
		 * Set<Category> childrenCategories = sortSubCategory(c.getChildren(), sortDir);
		 * 
		 * for (Category subcategory : childrenCategories) { String name = "--" +
		 * subcategory.getName(); hierarchailCategories.add(Category.full(subcategory,
		 * name)); subhirarichal(hierarchailCategories, subcategory, 1, sortDir); } }
		 */
		
		return hierarchailCategories;
	}

	private void subhirarichal(List<Category> hierarchialCategories, Category parent, int level, String sortDir) {
		Set<Category> child = sortSubCategory(parent.getChildren(), sortDir);
		
		int sublevel = level + 1;
		
		for (Category cat : child) {
			String name = "";
			for (int i = 0; i < level; i++) {
				name += "--";
			}
			name += cat.getName();
			hierarchialCategories.add(Category.full(cat, name));
			subhirarichal(hierarchialCategories, cat, sublevel, sortDir);
		}
	}

	public void checkEnabledStatus(Integer id, boolean enabled) {		
		categoryRepository.updateEnabledStatus(id, enabled);
	}

	public List<Category> allCategoryForForm() {
		List<Category> categoryInForm = new ArrayList<>();
		Iterable<Category> categoryInDb = categoryRepository.findAll(Sort.by("name").ascending());
		
		for (Category category : categoryInDb) {
			if (category.getParent() == null) {
				categoryInForm.add(Category.formCategory(category));

				Set<Category> child = sortSubCategory(category.getChildren());

				for (Category subCategory : child) {
					String name = "--" + subCategory.getName();
					categoryInForm.add(Category.formCategory(subCategory.getId(), name));
					listChildren(categoryInForm, subCategory, 1);
				}
			}
		}
		return categoryInForm;
	}

	private void listChildren(List<Category> categoryInForm, Category parent, int sublevel) {
		int newLevel = sublevel + 1;
		Set<Category> children = sortSubCategory(parent.getChildren());
		for (Category child : children) {
			String name = "";
			for (int i = 0; i < newLevel; i++) {
				name += "--";
			}
			name += child.getName();
			categoryInForm.add(Category.formCategory(child.getId(), name));
			listChildren(categoryInForm, child, newLevel);
		}

	}

	public Category saveCategory(Category category) {
		return categoryRepository.save(category);
	}

	public Category getCategory(int id) throws NoCategoryException {
		try {
			return categoryRepository.findById(id).get();
		} catch (NoSuchElementException e) {
			throw new NoCategoryException("Could not find category with id " + id);
		}
	}

	public String checkUnique(Integer id, String name, String alias) {

		boolean isCreatingNew = (id == null || id == 0);
		Category categoryByName = categoryRepository.findByName(name);
		if (isCreatingNew) {
			if (categoryByName != null) {
				return "Duplicate Name";
			} else {
				Category categoryByAlias = categoryRepository.findByAlias(alias);
				if (categoryByAlias != null) {
					return "Duplicate Alias";
				}
			}
		} else {
			if (categoryByName != null && categoryByName.getId() != id) {
				return "Duplicate Name";
			}
			Category categoryByAlias = categoryRepository.findByAlias(alias);
			if (categoryByAlias != null && categoryByAlias.getId() != id) {
				return "Duplicate Name";
			}
		}
		return "OK";
	}

	private SortedSet<Category> sortSubCategory(Set<Category> children) {
		return sortSubCategory(children, "asc");
	}

	private SortedSet<Category> sortSubCategory(Set<Category> children, String sortDir) {
		SortedSet<Category> sortedSet = new TreeSet<>(new Comparator<Category>() {

			@Override
			public int compare(Category cat1, Category cat2) {
				if (sortDir.equals("asc")) {
					return cat1.getName().compareTo(cat2.getName());
				}
				return cat2.getName().compareTo(cat1.getName());
			}
		});
		
		sortedSet.addAll(children);
		return sortedSet;
	}

	public void deleteCategory(int id) throws CategoryNotFoundException {
		Long countById=categoryRepository.countById(id);
		if(countById==null || countById==0) {
			throw new CategoryNotFoundException("No Such category is present.");
		}
		categoryRepository.deleteById(id);
		
	}
}
