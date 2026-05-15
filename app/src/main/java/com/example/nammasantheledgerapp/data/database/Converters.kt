package com.example.nammasantheledgerapp.data.database

import androidx.room.TypeConverter
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromTransactionType(type: TransactionType): String {
        return type.name
    }

    @TypeConverter
    fun toTransactionType(type: String): TransactionType {
        return TransactionType.valueOf(type)
    }

    @TypeConverter
    fun fromPaymentMethod(method: PaymentMethod): String {
        return method.name
    }

    @TypeConverter
    fun toPaymentMethod(method: String): PaymentMethod {
        return PaymentMethod.valueOf(method)
    }

    @TypeConverter
    fun fromTrustLevel(level: TrustLevel): String {
        return level.name
    }

    @TypeConverter
    fun toTrustLevel(level: String): TrustLevel {
        return TrustLevel.valueOf(level)
    }
}