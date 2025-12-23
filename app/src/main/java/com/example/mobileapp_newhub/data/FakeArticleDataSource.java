package com.example.mobileapp_newhub.data;

import com.example.mobileapp_newhub.model.Post;

import java.util.ArrayList;
import java.util.List;

public class FakeArticleDataSource {

    public static List<Post> getTopPosts() {
        List<Post> list = new ArrayList<>();

        Post p1 = new Post();
        p1.setId("1");
        p1.setTitle("Tin nóng: Giá vàng giảm mạnh");
        p1.setImageUrl("gold");
        p1.setContent("Giá vàng hôm nay tiếp tục giảm sâu do áp lực từ thị trường quốc tế. Các nhà đầu tư đang thận trọng quan sát các động thái tiếp theo của Cục Dự trữ Liên bang Mỹ (Fed).");
        p1.setCategoryName("Kinh tế");
        p1.setCategoryId("kinh_te");
        p1.setTimestamp(System.currentTimeMillis());
        list.add(p1);

        Post p2 = new Post();
        p2.setId("2");
        p2.setTitle("Công nghệ AI bùng nổ tại Việt Nam");
        p2.setImageUrl("ai");
        p2.setContent("Nhiều doanh nghiệp Việt Nam đang ứng dụng mạnh mẽ AI vào quy trình sản xuất và kinh doanh, giúp tăng năng suất lao động và tối ưu hóa chi phí.");
        p2.setCategoryName("Công nghệ");
        p2.setCategoryId("cong_nghe");
        p2.setTimestamp(System.currentTimeMillis());
        list.add(p2);

        Post p3 = new Post();
        p3.setId("3");
        p3.setTitle("Học Android Studio cấp tốc");
        p3.setImageUrl("android");
        p3.setContent("Hướng dẫn chi tiết cách xây dựng ứng dụng Android từ cơ bản đến nâng cao. Khám phá các công cụ mới nhất trong Android Studio để phát triển app hiệu quả.");
        p3.setCategoryName("Giáo dục");
        p3.setCategoryId("giao_duc");
        p3.setTimestamp(System.currentTimeMillis());
        list.add(p3);

        Post p4 = new Post();
        p4.setId("4");
        p4.setTitle("Đội tuyển Việt Nam chuẩn bị cho giải đấu mới");
        p4.setImageUrl("football");
        p4.setContent("Huấn luyện viên trưởng đã công bố danh sách tập trung các cầu thủ. Toàn đội đang tích cực tập luyện để hướng tới những mục tiêu cao hơn.");
        p4.setCategoryName("Thể thao");
        p4.setCategoryId("the_thao");
        p4.setTimestamp(System.currentTimeMillis());
        list.add(p4);

        Post p5 = new Post();
        p5.setId("5");
        p5.setTitle("Du lịch Việt Nam phục hồi mạnh mẽ");
        p5.setImageUrl("travel");
        p5.setContent("Lượng khách quốc tế đến Việt Nam tăng trưởng vượt bậc trong quý này. Các địa điểm du lịch nổi tiếng đang thu hút đông đảo du khách trở lại.");
        p5.setCategoryName("Du lịch");
        p5.setCategoryId("du_lich");
        p5.setTimestamp(System.currentTimeMillis());
        list.add(p5);

        Post p6 = new Post();
        p6.setId("6");
        p6.setTitle("Bí quyết sống khỏe mỗi ngày");
        p6.setImageUrl("health");
        p6.setContent("Chế độ ăn uống và tập luyện đóng vai trò quan trọng trong việc duy trì sức khỏe. Hãy cùng khám phá những thói quen tốt giúp bạn luôn tràn đầy năng lượng.");
        p6.setCategoryName("Sức khỏe");
        p6.setCategoryId("suc_khoe");
        p6.setTimestamp(System.currentTimeMillis());
        list.add(p6);

        Post p7 = new Post();
        p7.setId("7");
        p7.setTitle("Ra mắt mẫu xe điện mới");
        p7.setImageUrl("car");
        p7.setContent("Mẫu xe điện thế hệ mới với nhiều tính năng thông minh vừa được trình làng. Đây là bước tiến quan trọng trong việc thúc đẩy giao thông xanh.");
        p7.setCategoryName("Ô tô");
        p7.setCategoryId("o_to");
        p7.setTimestamp(System.currentTimeMillis());
        list.add(p7);

        Post p8 = new Post();
        p8.setId("8");
        p8.setTitle("Thị trường bất động sản có dấu hiệu ấm lại");
        p8.setImageUrl("property");
        p8.setContent("Các giao dịch bất động sản tại các thành phố lớn bắt đầu tăng trưởng trở lại sau thời gian im ắng. Nhiều dự án mới đang được đẩy nhanh tiến độ.");
        p8.setCategoryName("Kinh doanh");
        p8.setCategoryId("kinh_doanh");
        p8.setTimestamp(System.currentTimeMillis());
        list.add(p8);

        return list;
    }
}
