package com.bookservice.entity;

import javax.persistence.Column;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Books {
		
		public String isbn;
		
		public String title;
		
		public String publishedDate;
		
		public long totalCopies;
		
		public long issuedCopies;
		
		public String author;
		
	}
