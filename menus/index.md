---
title: Menus
layout: default
main: true
description: Explains what each menu item in Protégé does
---

# Menu Items

<div style="border-bottom: 1px solid #f0f0f0; padding-bottom: 10px; margin-bottom: 10px;">
	<div>
		Filter by menu:
		<select id="menuFilter"></select>
	</div>
</div>

<script>
	var menuUtil = (function() {
		var menus = [];
		menus.push("");
		$("#menuFilter").on("change", function(e) {
			var selectedMenu = $("#menuFilter").find(":selected").text();
			if (selectedMenu === "") {
				$(".menu-item").show(400);
			} else {
				$(".menu-item[data-parent!=" + selectedMenu + "]").hide(400);
				$("[data-parent=" + selectedMenu + "]").show(400);
			}
		});


		return {
			addMenu: function(menu) {
				if ($.inArray(menu, menus) === -1) {
					menus.push(menu);
				}
			},
			getMenus: function() {
				return menus;
			},
			installMenus: function() {
				var selectElement = $("#menuFilter");
				for (var i = 0; i < menus.length; i++) {
					var menu = menus[i];
					$("<option/>", {
						value: menu,
						html: menu
					}).appendTo(selectElement);
				}
			}
		};
	})();
</script>

<div>
	{% assign sorted_menus = (site.menus | sort: 'title') %} {% for menu in sorted_menus %}
	<script>
		menuUtil.addMenu("{{menu.parent}}")
	</script>
	<div class="menu-item" data-parent="{{menu.parent}}" style="padding-top: 10px; padding-bottom: 30px;">
		<div style="font-weight: bold;">
			{{menu.title}} {% if menu.accelerator %}
			<span style="font-weight: 300; font-size: 12px; color: gray;">
				<span style="padding: 0 0 10px 10px">
					Windows: <span class="accelerator">Ctrl-{{menu.accelerator  | replace: 'Shift', '&#x21E7;'}}</span>
			</span>
			<span style="padding: 0 0 10px 10px">
					Mac: <span class="accelerator">&#x2318;{{menu.accelerator  | replace: 'Shift', '&#x21E7;'}}</span>
			</span>
			</span>
			{% endif %}
			<div style="font-size: smaller; color: gray; font-weight: 300;">
				{{menu.parent}} > {{menu.title}}
			</div>
		</div>

		<div>
			{{menu.content}}
		</div>

	</div>
	{% endfor %}
</div>

<script>
	menuUtil.installMenus();
</script>
