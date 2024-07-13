{
	"format_version": "1.12.0",
	"minecraft:geometry": [
		{
			"description": {
				"identifier": "geometry.Door-Closed - Converted",
				"texture_width": 256,
				"texture_height": 256,
				"visible_bounds_width": 2,
				"visible_bounds_height": 3.5,
				"visible_bounds_offset": [0, 1.25, 0]
			},
			"bones": [
				{
					"name": "LEF",
					"pivot": [0, 8, 0],
					"cubes": [
						{"origin": [4, 3, -4], "size": [4, 7, 8], "uv": [24, 18]},
						{"origin": [4, 10, -4], "size": [2, 1, 8], "uv": [62, 60]},
						{"origin": [4, 11, -4], "size": [1, 1, 8], "uv": [74, 15]},
						{
							"origin": [-3, 3, -4],
							"size": [7, 13, 8],
							"uv": {
								"north": {"uv": [48, 39], "uv_size": [7, 15]},
								"east": {"uv": [40, 33], "uv_size": [8, 15]},
								"south": {"uv": [63, 39], "uv_size": [7, 15]},
								"west": {"uv": [55, 39], "uv_size": [8, 15]},
								"up": {"uv": [48, 25], "uv_size": [7, 8]},
								"down": {"uv": [55, 33], "uv_size": [7, -8]}
							}
						},
						{"origin": [-2, 1, -4], "size": [10, 2, 8], "uv": [0, 33]},
						{"origin": [0, 0, -4], "size": [8, 1, 8], "uv": [0, 43]},
						{
							"origin": [-4, 5, -4],
							"size": [1, 11, 8],
							"uv": {
								"north": {"uv": [8, 83], "uv_size": [1, 13]},
								"east": {"uv": [0, 83], "uv_size": [8, 13]},
								"south": {"uv": [17, 83], "uv_size": [1, 13]},
								"west": {"uv": [9, 83], "uv_size": [8, 13]},
								"up": {"uv": [8, 71], "uv_size": [1, 8]},
								"down": {"uv": [9, 79], "uv_size": [1, -8]}
							}
						},
						{"origin": [-5, 8, -4], "size": [1, 8, 8], "uv": [34, 64]}
					]
				},
				{
					"name": "HOTWHEEL2",
					"parent": "LEF",
					"pivot": [8, 14, 0],
					"cubes": [
						{
							"origin": [6, 10, -4],
							"size": [2, 6, 8],
							"uv": {
								"north": {"uv": [8, 10], "uv_size": [2, 6]},
								"east": {"uv": [0, 10], "uv_size": [8, 6]},
								"south": {"uv": [18, 10], "uv_size": [2, 6]},
								"west": {"uv": [10, 10], "uv_size": [8, 6]},
								"up": {"uv": [8, 0], "uv_size": [2, 8]},
								"down": {"uv": [10, 8], "uv_size": [2, -8]}
							}
						},
						{
							"origin": [5, 11, -4],
							"size": [1, 5, 8],
							"uv": {
								"north": {"uv": [28, 9], "uv_size": [1, 5]},
								"east": {"uv": [20, 9], "uv_size": [8, 5]},
								"south": {"uv": [37, 9], "uv_size": [1, 5]},
								"west": {"uv": [29, 9], "uv_size": [8, 5]},
								"up": {"uv": [28, 0], "uv_size": [1, 8]},
								"down": {"uv": [29, 8], "uv_size": [1, -8]}
							}
						},
						{"origin": [4, 12, -4], "size": [1, 4, 8], "uv": [30, 6]}
					]
				},
				{
					"name": "RAJ",
					"pivot": [0, 8, 0]
				},
				{
					"name": "HOTWHEEL",
					"parent": "RAJ",
					"pivot": [8, 14, 0]
				},
				{
					"name": "PRETZEL",
					"pivot": [0, 8, 0],
					"cubes": [
						{"origin": [-7, 5, -8], "size": [2, 3, 16], "uv": [84, 0]},
						{"origin": [-6, 3, -8], "size": [2, 2, 16], "uv": [78, 100]},
						{"origin": [-5, 1, -8], "size": [2, 2, 16], "uv": [78, 100]},
						{"origin": [-4, 0, -8], "size": [3, 1, 16], "uv": [78, 37]},
						{"origin": [-3, 1, -8], "size": [1, 1, 16], "uv": [78, 54]},
						{"origin": [-8, 8, -8], "size": [2, 8, 16], "uv": [98, 87]}
					]
				},
				{
					"name": "TALAREKBUZIA",
					"pivot": [8, 0, -8],
					"cubes": [
						{"origin": [-6, 8, -6], "size": [1, 8, 1.75], "uv": [24, 14]},
						{"origin": [-5, 5, -6], "size": [1, 3, 1.75], "uv": [16, 0]},
						{"origin": [-4, 3, -6], "size": [1, 2, 1.75], "uv": [12, 0]},
						{"origin": [-1, 0, -6], "size": [1, 1, 1.75], "uv": [0, 0]},
						{"origin": [-2, 1, -6], "size": [1, 1, 1.75], "uv": [3, 1]},
						{"origin": [-3, 2, -6], "size": [1, 1, 1.75], "uv": [0, 2]}
					]
				},
				{
					"name": "TALAREKPUPA",
					"pivot": [8, 0, -8],
					"cubes": [
						{"origin": [-6, 8, 4.25], "size": [1, 8, 1.75], "uv": [24, 14]},
						{"origin": [-5, 5, 4.25], "size": [1, 3, 1.75], "uv": [16, 4]},
						{"origin": [-4, 3, 4.25], "size": [1, 2, 1.75], "uv": [12, 3]},
						{"origin": [-2, 1, 4.25], "size": [1, 1, 1.75], "uv": [0, 4]},
						{"origin": [-1, 0, 4.25], "size": [1, 1, 1.75], "uv": [0, 6]},
						{"origin": [-3, 2, 4.25], "size": [1, 1, 1.75], "uv": [3, 3]}
					]
				},
				{
					"name": "GRUBYTUTESTURYNIEMA",
					"pivot": [8, 0, -8],
					"cubes": [
						{
							"origin": [-8, 0, -8],
							"size": [4, 1, 16],
							"uv": {
								"north": {"uv": [0, 132.5], "uv_size": [2, 0.5]},
								"east": {"uv": [0, 132.5], "uv_size": [8, 0.5]},
								"south": {"uv": [6, 132.5], "uv_size": [2, 0.5]},
								"west": {"uv": [22, 161], "uv_size": [16, 1]},
								"up": {"uv": [18, 145], "uv_size": [4, 16]},
								"down": {"uv": [0, 133], "uv_size": [2, -8]}
							}
						},
						{
							"origin": [-8, 1, -8],
							"size": [3, 2, 16],
							"uv": {
								"north": {"uv": [0, 131.5], "uv_size": [1.5, 1]},
								"east": {"uv": [0, 131.5], "uv_size": [8, 1]},
								"south": {"uv": [6.5, 131.5], "uv_size": [1.5, 1]},
								"west": {"uv": [21, 161], "uv_size": [16, 2]},
								"up": {"uv": [18, 145], "uv_size": [3, 16]},
								"down": {"uv": [21, 161], "uv_size": [3, -16]}
							}
						},
						{
							"origin": [-8, 3, -8],
							"size": [2, 2, 16],
							"uv": {
								"north": {"uv": [0, 130.5], "uv_size": [1, 1]},
								"east": {"uv": [0, 130.5], "uv_size": [8, 1]},
								"south": {"uv": [7, 130.5], "uv_size": [1, 1]},
								"west": {"uv": [20, 161], "uv_size": [16, 2]},
								"up": {"uv": [18, 145], "uv_size": [2, 16]},
								"down": {"uv": [20, 161], "uv_size": [2, -16]}
							}
						},
						{
							"origin": [-8, 5, -8],
							"size": [1, 3, 16],
							"uv": {
								"north": {"uv": [0, 129], "uv_size": [0.5, 1.5]},
								"east": {"uv": [0, 129], "uv_size": [8, 1.5]},
								"south": {"uv": [7.5, 129], "uv_size": [0.5, 1.5]},
								"west": {"uv": [19, 161], "uv_size": [16, 3]},
								"up": {"uv": [18, 145], "uv_size": [1, 16]},
								"down": {"uv": [19, 161], "uv_size": [1, -16]}
							}
						}
					]
				}
			]
		}
	]
}