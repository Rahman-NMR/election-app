package com.rahman.pemiluapp.domain.util

import com.rahman.pemiluapp.data.model.VoterDataModel
import com.rahman.pemiluapp.utils.DateFormatter.tryFormatTimestamp

object VoterDataProcessor {
    fun processVoters(voters: List<VoterDataModel>): List<VoterDataModel> {
        return voters
            .sortedByDescending { it.tanggal?.takeIf { date -> date.isNotEmpty() } ?: it.nama }
            .map { it.copy(tanggal = tryFormatTimestamp(it.tanggal)) }
    }
}