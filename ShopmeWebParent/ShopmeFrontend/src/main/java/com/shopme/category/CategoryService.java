package com.shopme.category;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Category;

@Service
public class CategoryService {

	@Autowired
	private CategoryRepository categoryRepository;
	
	public List<Category> listNoChildrenCategory(){
		List<Category>listNoChildCategory=new ArrayList<>();
		List<Category>enabledCategories=categoryRepository.findAllEnabled();
		enabledCategories.forEach(cat->{
			Set<Category>children=cat.getChildren();
			if(children==null || children.size()==0) {
				listNoChildCategory.add(cat);
			}
		});
		return listNoChildCategory;
	}
	
	public Category getCategory(String alias) throws CategoryNotFoundExeption {
		Category category= categoryRepository.findByAliasEnabled(alias);
		if(category==null) {
			throw new CategoryNotFoundExeption("No Category Available of name "+alias);
		}
		return category;
	}
	
	public List<Category> getParent(Category child){
		List<Category>listParents=new ArrayList<Category>();
		Category parent=child.getParent();
		while(parent!=null) {
			listParents.add(0, parent);
			parent=parent.getParent();
		}
		listParents.add(child);
		return listParents;
	}
}
