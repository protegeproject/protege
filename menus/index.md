---
title: Menus
layout: default
main: true
sortingIndex: 2
description: Explains what each menu item in Protégé does
---
# Menu Items
<div>
{% assign sorted_menus = (site.menus | sort: 'title') %}
{% for menu in sorted_menus %}
	<div style="padding: 10px;">
		<div style="font-weight: bold;">
			{{menu.parent}}  |  {{menu.title}}
			<span style="font-weight: 300; font-size: 12px; color: gray;">
				<span style="padding: 0 0 10px 10px">
					Windows: <b>Ctrl-{{menu.accelerator  | replace: 'Shift', '&#x21E7;'}}</b>
				</span>
				<span  style="padding: 0 0 10px 10px">
					Mac: <b>&#x2318;{{menu.accelerator  | replace: 'Shift', '&#x21E7;'}}</b>
				</span>
			</span>
		</div>

		<div>
			{{menu.content}}
		</div>

	</div>
{% endfor %}
</div>
