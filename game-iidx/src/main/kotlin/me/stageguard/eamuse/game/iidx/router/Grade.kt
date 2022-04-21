/*
 * Copyright (c) 2022 StageGuard
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.stageguard.eamuse.game.iidx.router

import com.buttongames.butterflycore.xml.kbinxml.KXmlBuilder
import io.netty.handler.codec.http.HttpResponseStatus
import me.stageguard.eamuse.database.Database
import me.stageguard.eamuse.game.iidx.IIDXGradeRouteHandler
import me.stageguard.eamuse.game.iidx.LDJ20211013
import me.stageguard.eamuse.game.iidx.model.*
import me.stageguard.eamuse.server.InvalidRequestException
import me.stageguard.eamuse.server.RouteModel
import org.ktorm.dsl.and
import org.ktorm.dsl.eq
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf
import org.w3c.dom.Element
import kotlin.math.max

@RouteModel(LDJ20211013)
object Raised : IIDXGradeRouteHandler("raised") {
    override suspend fun handle(node: Element): KXmlBuilder {
        val refId = Database.query { db ->
            db.sequenceOf(UserProfileTable).find {
                it.iidxId eq (node.getAttribute("iidxid").toIntOrNull()
                    ?: throw InvalidRequestException(HttpResponseStatus.BAD_REQUEST))
            }?.refId
        } ?: throw InvalidRequestException(HttpResponseStatus.BAD_REQUEST)

        val style =
            node.getAttribute("gtype").toIntOrNull() ?: throw InvalidRequestException(HttpResponseStatus.BAD_REQUEST)
        val gid =
            node.getAttribute("gid").toIntOrNull() ?: throw InvalidRequestException(HttpResponseStatus.BAD_REQUEST)
        val cStage =
            node.getAttribute("cStage").toIntOrNull() ?: throw InvalidRequestException(HttpResponseStatus.BAD_REQUEST)
        val achi =
            node.getAttribute("achi").toIntOrNull() ?: throw InvalidRequestException(HttpResponseStatus.BAD_REQUEST)

        val grade = Database.query { db ->
            db.sequenceOf(GradeTable).find { it.refId eq refId and (it.style eq style) and (it.grade eq gid) }
        }

        if (grade != null) {
            if (cStage <= grade.dArray[2]) {
                grade.dArray = listOf(style, gid, grade.dArray[2], max(achi, grade.dArray[3]))
            }
            grade.flushChanges()
        } else {
            GradeTable.insert(Grade {
                this.refId = refId
                this.style = style
                this.grade = gid
                this.dArray = listOf(style, gid, cStage, achi)
            })
        }

        if (cStage == 4) {
            val pcData = Database.query { db -> db.sequenceOf(PCDataTable).find { it.refId eq refId } }
                ?: throw InvalidRequestException(HttpResponseStatus.BAD_REQUEST)

            when (style) {
                0 -> pcData.sgid = max(pcData.sgid, gid)
                1 -> pcData.dgid = max(pcData.dgid, gid)
            }
            pcData.flushChanges()
        }

        return createResponseNode().a("pnum", "1337")
    }
}
