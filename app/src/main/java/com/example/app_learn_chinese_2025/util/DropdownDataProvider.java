package com.example.app_learn_chinese_2025.util;

import com.example.app_learn_chinese_2025.model.data.CapDoHSK;
import com.example.app_learn_chinese_2025.model.data.LoaiBaiGiang;
import com.example.app_learn_chinese_2025.model.data.ChuDe;

import java.util.ArrayList;
import java.util.List;


public class DropdownDataProvider {

    /**
     * Lấy danh sách cấp độ HSK
     */
    public static List<CapDoHSK> getCapDoHSKList() {
        List<CapDoHSK> list = new ArrayList<>();
        list.add(new CapDoHSK(1, "HSK 1"));
        list.add(new CapDoHSK(2, "HSK 2"));
        list.add(new CapDoHSK(3, "HSK 3"));
        list.add(new CapDoHSK(4, "HSK 4"));
        list.add(new CapDoHSK(5, "HSK 5"));
        list.add(new CapDoHSK(6, "HSK 6"));
        return list;
    }

    /**
     * Lấy danh sách loại bài giảng
     */
    public static List<LoaiBaiGiang> getLoaiBaiGiangList() {
        List<LoaiBaiGiang> list = new ArrayList<>();
        list.add(new LoaiBaiGiang(1, "Video", "Bài giảng có video"));
        list.add(new LoaiBaiGiang(2, "Audio", "Bài giảng chỉ có âm thanh"));
        list.add(new LoaiBaiGiang(3, "Văn bản", "Bài giảng dạng text"));
        list.add(new LoaiBaiGiang(4, "Tương tác", "Bài giảng có bài tập"));
        return list;
    }

    /**
     * Lấy danh sách chủ đề
     */
    public static List<ChuDe> getChuDeList() {
        List<ChuDe> list = new ArrayList<>();
        list.add(new ChuDe(1, "Giao tiếp cơ bản", "Chào hỏi, giới thiệu", null));
        list.add(new ChuDe(2, "Gia đình", "Về gia đình và người thân", null));
        list.add(new ChuDe(3, "Công việc", "Về nghề nghiệp và công việc", null));
        list.add(new ChuDe(4, "Du lịch", "Du lịch và giao thông", null));
        list.add(new ChuDe(5, "Ẩm thực", "Đồ ăn và thức uống", null));
        list.add(new ChuDe(6, "Mua sắm", "Mua sắm và thanh toán", null));
        list.add(new ChuDe(7, "Sức khỏe", "Y tế và sức khỏe", null));
        list.add(new ChuDe(8, "Thời tiết", "Khí hậu và thời tiết", null));
        return list;
    }
}