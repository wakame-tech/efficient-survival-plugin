import open3d as o3d

pcd = o3d.io.read_point_cloud("cave.xyz", format='xyz')
# o3d.io.write_point_cloud("cave.xyz", pcd)
o3d.visualization.draw_geometries([pcd])