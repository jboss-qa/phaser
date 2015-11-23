package org.jboss.qa.phaser.point;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
public class InjectionPoint extends AbstractInjectionPoint {

	@Getter @Setter private String content;
}
