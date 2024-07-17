package me.parkhuijun.commons;

public class Constant {
    public static final int SMS_DIGITS = 6;

    public static class TOKEN {
        public static final int SUCCESS = 200;
        public static final int EXPIRED = 401;
        public static final int MALFORMED = 400;
        public static final int EXCEPTION = 500;
    }

//    public static class KafkaEvent {
//        public static final String DATA_CHECK_COMPLETE = "DataCheckComplete";
//        public static final String DATA_CHECK_WARNING = "DataCheckWarning";
//        public static final String DATA_CHECK_ERROR = "DataCheckError";
//        public static final String TRAINING = "Training";
//        public static final String TRAINING_COMPLETE = "TrainingComplete";
//        public static final String FILE_UPLOAD_COMPLETE = "FileUploadComplete";
//        public static final String TABULAR_DATASET_UPLOAD_COMPLETE = "TabularDatasetUploadComplete";
//        public static final String TABULAR_MODEL_TRAINING = "TabularModelTraining";
//        public static final String TABULAR_MODEL_TRAINING_COMPLETE = "TabularModelTrainingComplete";
//    }
}
