package com.bus.entities;

import lombok.Data;
import javax.persistence.*;

@Entity
@Data
public class Feedback {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "rating_content")
	private String ratingContent;
}
