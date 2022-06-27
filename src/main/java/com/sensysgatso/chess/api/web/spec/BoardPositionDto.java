package com.sensysgatso.chess.api.web.spec;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class BoardPositionDto{
	
	@NotNull(message = "X axis is mandatory")
	private Integer x;
	
	@NotNull(message = "Y axis is mandatory")
	private Integer y;
}