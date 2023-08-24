package com.fin.love.repository.assessment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "ASSESSMENT")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Assessment {
	
	@Id
	private String id; // PK
	
	@Column(nullable = true)
	private int sexy; 
	
	@Column(nullable = true)
	private int beautiful;
	
	@Column(nullable = true)
	private int cute;
	
	@Column(nullable = true)
	private int handsome;
	
	@Column(nullable = true)
	private int wonderful;
	
	
	public Assessment sexyUpdate(int sexy) {
		this.sexy = sexy;
		
		return this;
	}
	
	public Assessment beautifulUpdate(int beautiful) {
		this.beautiful = beautiful;
		
		return this;
	}
	
	public Assessment cuteUpdate(int cute) {
		this.cute = cute;
		
		return this;
	}
	
	public Assessment wonderfulUpdate(int wonderful) {
		this.wonderful = wonderful;
		
		return this;
	}
	
	public Assessment handsomeUpdate(int handsome) {
		this.handsome = handsome;
		
		return this;
	}
	
}
