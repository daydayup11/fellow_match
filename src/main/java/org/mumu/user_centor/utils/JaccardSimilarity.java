package org.mumu.user_centor.utils;

import org.mumu.user_centor.model.domain.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 如果相同的标签有不同的词语表示，那么在使用 Jaccard 算法进行相似度计算时，这些标签就会被认为是不同的标签，
 * 导致相似度计算不准确。为了解决这个问题，可以对输入的标签进行预处理，
 * 将不同的词语表示转换成相同的表示形式，比如使用同义词或者近义词进行替换，
 * 或者使用文本聚类或者主题建模等技术进行文本分类和分析。
 *
 * 另外，可以考虑使用基于文本向量化的方法来进行标签的相似度计算，
 * 比如使用 TF-IDF、Word2Vec 或者 Bert 等模型将文本转换成向量，然后计算向量之间的相似度。
 * 这种方法可以解决标签的不同词语表示问题，同时也可以更准确地反映标签之间的相似度。
 * 如果使用这种方法，可以考虑使用 Cosine 相似度或者欧几里得距离等指标来衡量标签之间的相似度。
 */
public class JaccardSimilarity {

    //需要注意的是，在实际应用中，可能需要将 Jaccard 相似度算法与其他算法结合使用，以得到更准确的推荐结果。
    // 此外，还需要考虑到数据量的大小和计算效率的问题，
    // 如果数据量很大，可能需要使用分布式计算和并行处理技术来提高计算效率。
    // 计算两个集合的 Jaccard 相似度
    private static double calculate(Set<String> set1, Set<String> set2) {
        Set<String> unionSet = new HashSet<String>(set1);
        unionSet.addAll(set2); // 取两个集合的并集
        Set<String> intersectionSet = new HashSet<String>(set1);
        intersectionSet.retainAll(set2); // 取两个集合的交集
        return (double) intersectionSet.size() / unionSet.size(); // 计算相似度
    }

    public static double matchUser(Set<String> userTags,Set<String> currentUserTags){
        // 计算与当前用户相似的其他用户
        return calculate(userTags, currentUserTags);
    }
    //bert模型结合余弦相似度计算优化
//    private static BertClient bertClient = new BertClient("localhost:5555");
//
//    public static float bertSimilarity(String a, String b) {
//        float[] aEmbedding = bertClient.encode(a);
//        float[] bEmbedding = bertClient.encode(b);
//        float dot = 0f, aNorm = 0f, bNorm = 0f;
//        for (int i = 0; i < aEmbedding.length; i++) {
//            dot += aEmbedding[i] * bEmbedding[i];
//            aNorm += aEmbedding[i] * aEmbedding[i];
//            bNorm += bEmbedding[i] * bEmbedding[i];
//        }
//        return dot / (float) (Math.sqrt(aNorm) * Math.sqrt(bNorm));
//    }


    // 测试
    public static void main(String[] args) {
        // 假设用户标签数据如下
        List<Set<String>> userTags = new ArrayList<Set<String>>();
        userTags.add(new HashSet<String>(){{add("游戏"); add("音乐"); add("电影");}});
        userTags.add(new HashSet<String>(){{add("美食"); add("旅游");}});
        userTags.add(new HashSet<String>(){{add("旅游"); add("运动");}});
        userTags.add(new HashSet<String>(){{add("游戏"); add("电影");}});
        userTags.add(new HashSet<String>(){{add("音乐"); add("电影");}});
        
        // 假设当前用户的标签数据如下
        Set<String> currentUserTags = new HashSet<String>(){{add("游戏"); add("电影");}};
        
        // 计算与当前用户相似的其他用户
        List<Integer> similarUsers = new ArrayList<Integer>();
        for (int i = 0; i < userTags.size(); i++) {
            double similarity = calculate(userTags.get(i), currentUserTags);
            if (similarity > 0.5) { // 只推荐与当前用户相似度大于 0.5 的用户
                similarUsers.add(i);
            }
        }
        // 输出推荐结果
        System.out.println("与当前用户相似的用户编号为：" + similarUsers.toString());
    }
}
