package com.snack.repository;

import com.snack.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.snack.entity.TypeArticle;
import java.util.Arrays;
import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
  List<Article> findByType(TypeArticle type);
  
}
